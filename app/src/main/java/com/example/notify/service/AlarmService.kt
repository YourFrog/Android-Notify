package com.example.notify.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import android.os.StrictMode
import io.reactivex.Observable
import android.app.NotificationManager
import com.example.notify.structure.NewsList
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import android.app.PendingIntent
import android.net.Uri
import io.reactivex.Single
import android.app.NotificationChannel
import android.graphics.*
import android.os.Build
import android.util.Base64
import android.widget.Toast
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.example.notify.R
import com.example.notify.structure.WeatherItem
import com.example.notify.structure.WeatherList
import okhttp3.Response
import okhttp3.internal.notifyAll


class AlarmService : Service() {

    companion object {
        const val NOTIFICATION_WEATHER_ID = -1;
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val result = super.onStartCommand(intent, flags, startId)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val settings = this@AlarmService.getSharedPreferences("notify", Context.MODE_PRIVATE)
        val editor = settings.edit()

        val minID = settings.getInt("max_id", 1700)
//        val minID = 1000
        val gson = Gson()

        Log.d("CheckNotify", "Current News ID $minID")

        getNews(minID)
            .subscribe({ content ->
                Log.d("XXX", content)

                val json = gson.fromJson(content, NewsList::class.java)

                json.maxBy { news -> news.id_news }?.let {news ->
                    editor.putInt("max_id", news.id_news)
                    editor.commit()
                }

                json.forEach { news ->

                    val observableImage = Single.just(Unit).subscribeOn(Schedulers.io()).map {
                        if( news.image == null ) {
                            Observable.just(Unit)
                        } else {
                            Picasso.get().load(news.image).get()
                        }
                    }

                    val observableText = Single.just(Unit).subscribeOn(Schedulers.io()).map {
                        var contentSubtitle = ArrayList<String>()

                        news.subtitle?.let { contentSubtitle.add(it) }
                        news.domain?.let { contentSubtitle.add(it) }

                        contentSubtitle.joinToString("\n")
                    }

                    Single.zip<Any, String, Boolean>(
                        observableImage,
                        observableText,
                        BiFunction { icon, bigText ->
                            val channelId = "Your_channel_id"
                            var notificationBuilder = NotificationCompat.Builder(this@AlarmService.applicationContext, channelId)

                            val style = when {
                                news.type == 2 -> {
                                    val weatherList = gson.fromJson(news.subtitle, WeatherList::class.java)
                                    val remoteViews = RemoteViews(this@AlarmService.applicationContext.packageName, R.layout.push_weather)

                                    var bitmap = textAsBitmap(this@AlarmService.applicationContext, weatherList.today.textIcon(), 72f, Color.BLACK)
                                    remoteViews.setImageViewBitmap(R.id.icon, bitmap)
                                    remoteViews.setTextViewText(R.id.description, weatherList.today.description())
                                    remoteViews.setTextViewText(R.id.main_temp_and_feels, weatherList.today.temperature() + "°")

                                    remoteViews.setTextViewText(R.id.wetness_description, weatherList.today.wetness() + "%")
                                    remoteViews.setTextViewText(R.id.wind_description, weatherList.today.wind() + " km/h")

                                    remoteViews.setTextViewText(R.id.sun_description, weatherList.today.sun.up + " / " + weatherList.today.sun.down)
                                    remoteViews.setTextViewText(R.id.moon_description, weatherList.today.moon.up + " / " + weatherList.today.moon.down)

                                    bitmap = textAsBitmap(this@AlarmService.applicationContext, weatherList.nextDays[0].textIcon(), 30f, Color.BLACK)
                                    remoteViews.setImageViewBitmap(R.id.first_day_image, bitmap)
                                    remoteViews.setTextViewText(R.id.first_day_temperature, weatherList.nextDays[0].temperature() + "°")
                                    remoteViews.setTextViewText(R.id.first_day_name, weatherList.nextDays[0].dayOfWeek)

                                    bitmap = textAsBitmap(this@AlarmService.applicationContext, weatherList.nextDays[1].textIcon(), 30f, Color.BLACK)
                                    remoteViews.setImageViewBitmap(R.id.second_day_image, bitmap)
                                    remoteViews.setTextViewText(R.id.second_day_temperature, weatherList.nextDays[1].temperature() + "°")
                                    remoteViews.setTextViewText(R.id.second_day_name, weatherList.nextDays[1].dayOfWeek)

                                    bitmap = textAsBitmap(this@AlarmService.applicationContext, weatherList.nextDays[2].textIcon(), 30f, Color.BLACK)
                                    remoteViews.setImageViewBitmap(R.id.third_day_image, bitmap)
                                    remoteViews.setTextViewText(R.id.third_day_temperature, weatherList.nextDays[2].temperature() + "°")
                                    remoteViews.setTextViewText(R.id.third_day_name, weatherList.nextDays[2].dayOfWeek)

                                    bitmap = textAsBitmap(this@AlarmService.applicationContext, weatherList.nextDays[3].textIcon(), 30f, Color.BLACK)
                                    remoteViews.setImageViewBitmap(R.id.fourth_day_image, bitmap)
                                    remoteViews.setTextViewText(R.id.fourth_day_temperature, weatherList.nextDays[3].temperature() + "°")
                                    remoteViews.setTextViewText(R.id.fourth_day_name, weatherList.nextDays[3].dayOfWeek)

                                    bitmap = textAsBitmap(this@AlarmService.applicationContext, weatherList.nextHours[0].textIcon(), 30f, Color.BLACK)
                                    remoteViews.setImageViewBitmap(R.id.first_hour_image, bitmap)
                                    remoteViews.setTextViewText(R.id.first_hour_temperature, weatherList.nextHours[0].temperature + "°")
                                    remoteViews.setTextViewText(R.id.first_hour_name, weatherList.nextHours[0].hour)

                                    bitmap = textAsBitmap(this@AlarmService.applicationContext, weatherList.nextHours[1].textIcon(), 30f, Color.BLACK)
                                    remoteViews.setImageViewBitmap(R.id.second_hour_image, bitmap)
                                    remoteViews.setTextViewText(R.id.second_hour_temperature, weatherList.nextHours[1].temperature + "°")
                                    remoteViews.setTextViewText(R.id.second_hour_name, weatherList.nextHours[1].hour)

                                    bitmap = textAsBitmap(this@AlarmService.applicationContext, weatherList.nextHours[2].textIcon(), 30f, Color.BLACK)
                                    remoteViews.setImageViewBitmap(R.id.third_hour_image, bitmap)
                                    remoteViews.setTextViewText(R.id.third_hour_temperature, weatherList.nextHours[2].temperature + "°")
                                    remoteViews.setTextViewText(R.id.third_hour_name, weatherList.nextHours[2].hour)

                                    bitmap = textAsBitmap(this@AlarmService.applicationContext, weatherList.nextHours[3].textIcon(), 30f, Color.BLACK)
                                    remoteViews.setImageViewBitmap(R.id.fourth_hour_image, bitmap)
                                    remoteViews.setTextViewText(R.id.fourth_hour_temperature, weatherList.nextHours[3].temperature + "°")
                                    remoteViews.setTextViewText(R.id.fourth_hour_name, weatherList.nextHours[3].hour)

                                    notificationBuilder
//                                        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                                        .setContent(remoteViews)
                                        .setCustomContentView(remoteViews)
                                        .setCustomBigContentView(remoteViews)
                                        .setSmallIcon(R.drawable.ic_forward_black_24dp)
                                        .setContentTitle(news.title)
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        .setOnlyAlertOnce(true)

                                    createNotificationChannel(channelId)

                                    NotificationManagerCompat
                                        .from(this.applicationContext)
                                        .notify(NOTIFICATION_WEATHER_ID, notificationBuilder.build())

                                    return@BiFunction true
                                }
                                icon is Bitmap -> {
                                    notificationBuilder.setContentText(news.subtitle ?: news.domain)
                                    notificationBuilder.setLargeIcon(icon)

                                    NotificationCompat.BigPictureStyle()
                                        .bigPicture(icon)
                                        .setBigContentTitle(news.title + "\n" + news.domain)
                                }
                                else -> {
                                    notificationBuilder.setContentText(bigText)

                                    NotificationCompat.BigTextStyle()
                                        .bigText(bigText)
                                }
                            }

                            val notificationIntent = Intent(Intent.ACTION_VIEW)

                            notificationIntent.data = Uri.parse(news.link)
                            val contentIntent = PendingIntent.getActivity(this@AlarmService, 0, notificationIntent, 0)

                            Log.d("XXX", "Utworzenie notyfikacji")
                            notificationBuilder
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentIntent(contentIntent)
                                .setContentTitle(news.title)
                                .setStyle(style)
                                .setPriority(NotificationCompat.PRIORITY_MAX)

                            createNotificationChannel(channelId)

                            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.notify(news.id_news, notificationBuilder.build())

                            true
                        }
                    ).subscribeOn(Schedulers.io()).subscribe({}, {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG)
                    })
                }

            Toast.makeText(this, "Pobieranie zakończone", Toast.LENGTH_LONG)
        }, {
            Toast.makeText(this, it.message, Toast.LENGTH_LONG)
            Log.e("ERROR", it.message)
        })

        return result
    }

    private fun createNotificationChannel(channelID: String)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "XXX"
            val descriptionText = "YYYY"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getNews(minID: Int): Single<String> {
        return Single.fromCallable {
            OkHttpClient().newCall(Request.Builder().url("http://yourfrog12.usermd.net/notify/api.php?id=$minID").get().build()).execute()
        }.map {
            it.body?.string() ?: ""
        }.doOnError {
            it.message?.let { message ->
                Log.e("ERROR", message)
            }
        }
    }

    private fun decodeImage(encode: String): Bitmap {
        val imageAsBytes = Base64.decode(encode, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
    }

    private fun textAsBitmap(context: Context, messageText: String, textSize: Float, textColor: Int): Bitmap {
       val font = ResourcesCompat.getFont(context, R.font.weathericons_regular_webfont)
       val paint = Paint()
       paint.textSize = textSize
       paint.typeface = font
       paint.setColor(textColor)
       paint.setTextAlign(Paint.Align.LEFT)

       val baseline = -paint.ascent() // ascent() is negative
       val width = (paint.measureText(messageText)+0.5f).toInt() // round
       val height = (baseline+paint.descent()+0.5f).toInt()

       val image = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
       val canvas = Canvas(image)
       canvas.drawText(messageText, 0.toFloat(), baseline,paint)

       return image
    }
}
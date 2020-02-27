package com.mxmariner.station

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding2.view.RxView
import com.mxmariner.di.Injector
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.mxtide.api.ITidesAndCurrents
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.mxtide.api.stationTypeFromString
import com.mxmariner.tides.R
import com.mxmariner.tides.extensions.formatDateTime
import com.mxmariner.tides.factory.StationPresentationFactory
import com.mxmariner.tides.model.StationPresentation
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_station.*
import org.joda.time.DateTime
import javax.inject.Inject

@Keep
class StationActivity : AppCompatActivity() {

  @Inject lateinit var tidesAndCurrents: ITidesAndCurrents
  @Inject lateinit var stationPresentationFactory: StationPresentationFactory
  private val compositeDisposable = CompositeDisposable()
  private val stationDate = BehaviorSubject.create<DateTime>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.activityInjector(this).inject(this)
    setContentView(R.layout.activity_station)

    //madrona://mxmariner.com/tides/station?stationName=NameUriEncoded
    val name = intent.data?.getQueryParameter("stationName")
    val stationType = stationTypeFromString(intent.data?.getQueryParameter("stationType"))

    compositeDisposable.addAll(
        getStationMessage(name, stationType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = ::bindUi,
                        onComplete = ::bindUiError
                ),

        RxView.clicks(editTime)
            .withLatestFrom(stationDate, BiFunction<Any, DateTime, DateTime> { _, date ->
              date
            }).flatMapMaybe {
              userTimePick(it)
            }.flatMapMaybe {
              getStationMessage(name, stationType, it)
            }.subscribeBy(
                onNext = this::bindUi
            ),

        RxView.clicks(editDate)
            .withLatestFrom(stationDate, BiFunction<Any, DateTime, DateTime> { _, date ->
              date
            })
            .flatMapMaybe {
              userDatePick(it)
            }.flatMapMaybe {
              getStationMessage(name, stationType, it)
            }.subscribeBy(
                onNext = this::bindUi
            )
    )
  }

  override fun onDestroy() {
    super.onDestroy()
    compositeDisposable.clear()
  }

  private fun getStationMessage(name: String?, type: StationType?, dateTime: DateTime = DateTime.now()): Maybe<StationPresentation> {
    return Maybe.create<IStation> { emitter ->
      tidesAndCurrents.findStationByName(name, type)?.let {
        emitter.onSuccess(it)
      } ?: {
        emitter.onComplete()
      }()
    }.map {
      val presentation = stationPresentationFactory.createPresentation(it, hrs = 12, dateTime = dateTime)
      stationDate.onNext(presentation.now)
      presentation
    }.subscribeOn(Schedulers.io())
  }

  @SuppressLint("SetTextI18n")
  private fun bindUi(presentation: StationPresentation) {
    icon.setImageResource(presentation.icon)

    nameAndTime.leftDesc = presentation.name
    nameAndTime.rightDesc = "${presentation.now.formatDateTime()}\n" +
        "${getString(R.string.scale)} ${presentation.scaleHours}${getString(R.string.hrs)}"

    positionAndTimeZone.leftDesc = presentation.position
    positionAndTimeZone.rightDesc = presentation.timeZone.toTimeZone().displayName

    distanceAndLevel.leftDesc = presentation.distance
    distanceAndLevel.rightDesc = presentation.predictionNow

    lineChart.applyPresentation(presentation)
  }

  private fun userTimePick(startDate: DateTime): Maybe<DateTime> {
    return Maybe.create<DateTime> { emitter ->
      var selectedDate: Pair<Int, Int>? = null
      val listener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        selectedDate = Pair(hourOfDay, minute)
      }
      TimePickerDialog(this, listener, startDate.hourOfDay, startDate.minuteOfHour, false).apply {
        setOnDismissListener {
          emitter.takeUnless { it.isDisposed }?.let {
            selectedDate?.let { (hour, minute) ->
              emitter.onSuccess(DateTime(startDate.year, startDate.monthOfYear, startDate.dayOfMonth, hour, minute, 0, startDate.zone))
            } ?: emitter.onComplete()
          }
        }
        show()
      }
    }.subscribeOn(AndroidSchedulers.mainThread())
  }

  private fun userDatePick(startDate: DateTime): Maybe<DateTime> {
    return Maybe.create<DateTime> { emitter ->
      var selectedDate: DateTime? = null
      val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        selectedDate = DateTime(year, month + 1, dayOfMonth, startDate.hourOfDay, startDate.minuteOfHour, startDate.secondOfMinute, startDate.zone)
      }
      DatePickerDialog(this, listener, startDate.year, startDate.monthOfYear - 1, startDate.dayOfMonth).apply {
        setOnDismissListener {
          emitter.takeUnless { it.isDisposed }?.let {
            selectedDate?.let {
              emitter.onSuccess(it)
            } ?: emitter.onComplete()
          }
        }
        show()
      }
    }.subscribeOn(AndroidSchedulers.mainThread())
  }

  private fun bindUiError() {
    messageLabel.text = getString(R.string.whoops)
  }
}

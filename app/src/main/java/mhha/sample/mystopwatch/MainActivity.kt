package mhha.sample.mystopwatch

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import mhha.sample.mystopwatch.databinding.ActivityMainBinding
import mhha.sample.mystopwatch.databinding.DialogCountdownSettingBinding
import java.util.Timer

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var countdownTime = 5
    private var currentcountdownDeciSecond = countdownTime * 10
    private var currenDeciSecond = 0
    private var timer : Timer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.countdownTextView.setOnClickListener{
            showCountdownSettingDialog()
        }//binding.countdownTextView.setOnClickListener

        binding.startButton.setOnClickListener {
            start()
            setButton(false)

        } //binding.startButton.setOnClickListener

        binding.stopButton.setOnClickListener {
            showAlerDialog()


        } //binding.stopButton.setOnClickListener

        binding.pauseButton.setOnClickListener {
            pause()
            setButton(true)

        } //binding.pauseButton.setOnClickListener

        binding.lapButton.setOnClickListener {
            if(currentcountdownDeciSecond == 0 ) lap()
        } //binding.lapButton.setOnClickListener

        initcountdownViews()

    } //onCreate

    private fun initcountdownViews(){
        binding.countdownTextView.text = String.format("%02d", countdownTime)
        currentcountdownDeciSecond = countdownTime * 10
        binding.countdownProgressBar.progress = 100
    } //initcountdownViews

    private fun setButton(click : Boolean){
        binding.startButton.isVisible = click
        binding.stopButton.isVisible = click
        binding.pauseButton.isVisible = !click
        binding.lapButton.isVisible = !click
    }

    private fun start(){
        timer = kotlin.concurrent.timer(initialDelay = 0, period = 100){
            if (currentcountdownDeciSecond == 0) {
                currenDeciSecond += 1

                val minutes = currenDeciSecond.div(10) / 60
                val seconds = currenDeciSecond.div(10) % 60
                val deciSeconds = currenDeciSecond % 10

                runOnUiThread {
                    binding.timeTextView.text = String.format("%02d:%02d", minutes, seconds)
                    binding.tickTextView.text = deciSeconds.toString()
                    binding.countdownGroup.isVisible = false
                }//runOnUiThread
            }else {
                currentcountdownDeciSecond -= 1
                val seconds = currentcountdownDeciSecond /10
                val progress = (currentcountdownDeciSecond/( countdownTime * 10f))*100
                binding.root.post {
                    binding.countdownTextView.text = String.format("%02d", seconds)
                    binding.countdownProgressBar.progress = progress.toInt()
                }//binding.root.post
            }
            //비프음
            if (currenDeciSecond == 0 && currentcountdownDeciSecond < 31 && currentcountdownDeciSecond % 10 == 0){
                val toneType = if(currentcountdownDeciSecond == 0){ToneGenerator.TONE_CDMA_HIGH_L}else {ToneGenerator.TONE_CDMA_ANSWER}
                ToneGenerator(AudioManager.STREAM_ALARM , 0)
                    .startTone( toneType, 100)
            }
        }//kotlin.concurrent.timer(initialDelay = 0, period = 100)
    } //private fun start()

    private fun stop(){
        setButton(true)
        currenDeciSecond = 0
        binding.timeTextView.text = "00:00"
        binding.tickTextView.text = "0"
        initcountdownViews()
        binding.countdownGroup.isVisible = true
        binding.lapContainerLinearLayout.removeAllViews()
    } //private fun stop()

    private fun pause(){
        timer?.cancel()
        timer = null
    } //private fun pause()

    private fun lap(){
        val container = binding.lapContainerLinearLayout
        val lapTextView = TextView(this).apply {
            textSize = 20f
            gravity = Gravity.CENTER
            val minutes = currenDeciSecond.div(10) / 60
            val seconds = currenDeciSecond.div(10) % 60
            val deciSeconds = currenDeciSecond % 10
            text = container.childCount.inc().toString()+ " : " + String.format(
                "%02d:%02d %01d", minutes , seconds , deciSeconds
            )
            setPadding(30)
        }.let { i ->
            container.addView( i, 0)
        }//TextView(this).apply
    } //private fun lap()

    private fun showCountdownSettingDialog(){

        AlertDialog.Builder(this).apply {
            val dialogBinding = DialogCountdownSettingBinding.inflate(layoutInflater)
            with(dialogBinding.countdownSecondPicker){
                maxValue = 20
                minValue = 0
                value=countdownTime
            }//with(dialogBinding.countdownSecondPicker)
            setTitle("시작 카운트 다운 설정")
            setView(dialogBinding.root)
            setPositiveButton("확인"){ _ , _ ->
                countdownTime = dialogBinding.countdownSecondPicker.value
                initcountdownViews()
            }//setPositiveButton("확인")
            setNegativeButton("취소"){ _ , _ ->
                initcountdownViews()
            }.show()
        } //AlertDialog.Builder(this).apply
    }//private fun showCountdownSettingDialog()

    private fun showAlerDialog(){
        AlertDialog.Builder(this).apply {
            setMessage("종료하시겠습니까?")
            setPositiveButton("네"){ _, _ ->
                stop()
            } //setPositiveButton("네",)
            setNegativeButton("아니요",null).show()
        } //AlertDialog.Builder(this).apply
    } //private fun showAlerDialog()

} //MainActivity
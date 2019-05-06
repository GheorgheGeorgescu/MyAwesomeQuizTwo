package com.example.myawesomequiztwo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
//import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_quiz.*
import java.util.*

class QuizActivity : AppCompatActivity() {

    private var textViewQuestion: TextView? = null
    private var textViewScore: TextView? = null
    private var textViewQuestionCount: TextView? = null
    private var textViewCountDown: TextView? = null
    private var rbGroup: RadioGroup? = null
    private var rb1: RadioButton? = null
    private var rb2: RadioButton? = null
    private var rb3: RadioButton? = null
    private var buttonConfirmNext: Button? = null

    private var textColorDefaultRb: ColorStateList? = null
    private var textColorDefaultCd: ColorStateList? = null

    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0

    private var questionList: List<Question>? = null
    private var questionCounter: Int = 0
    private var questionCountTotal: Int = 0
    private var currentQuestion: Question? = null

    private var score: Int = 0
    private var answered: Boolean = false

    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        setSupportActionBar(toolbar)

       // fab.setOnClickListener { view ->
        //    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //        .setAction("Action", null).show()

            textViewQuestion = findViewById(R.id.text_view_question)
            textViewScore = findViewById(R.id.text_view_score)
            textViewQuestionCount = findViewById(R.id.text_view_question_count)
            textViewCountDown = findViewById(R.id.text_view_countdown)
            rbGroup = findViewById(R.id.radio_group)
            rb1 = findViewById(R.id.radio_button1)
            rb2 = findViewById(R.id.radio_button2)
            rb3 = findViewById(R.id.radio_button3)
            buttonConfirmNext = findViewById(R.id.button_confirm_next)

            textColorDefaultRb = rb1!!.textColors
            textColorDefaultCd = textViewCountDown!!.textColors

            val dbHelper = QuizDbHelper(this)
            questionList = dbHelper.allQuestions
            questionCountTotal = questionList!!.size
            Collections.shuffle(questionList)

            showNextQuestion()

            buttonConfirmNext!!.setOnClickListener {
                if (!answered) {
                    if (rb1!!.isChecked || rb2!!.isChecked || rb3!!.isChecked) {
                        checkAnswer()
                    } else {
                        Toast.makeText(this@QuizActivity, "Please select an answer", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    showNextQuestion()
                }
            }
        }

    private fun showNextQuestion() {
        rb1!!.setTextColor(textColorDefaultRb)
        rb2!!.setTextColor(textColorDefaultRb)
        rb3!!.setTextColor(textColorDefaultRb)
        rbGroup!!.clearCheck()

        if (questionCounter < questionCountTotal) {
            currentQuestion = questionList!![questionCounter]

            textViewQuestion!!.text = currentQuestion!!.question
            rb1!!.text = currentQuestion!!.option1
            rb2!!.text = currentQuestion!!.option2
            rb3!!.text = currentQuestion!!.option3

            questionCounter++
            textViewQuestionCount!!.text = "Question: $questionCounter/$questionCountTotal"
            answered = false
            buttonConfirmNext!!.text = "Confirm"

            timeLeftInMillis = COUNTDOWN_IN_MILLIS
            startCountDown()
        } else {
            finishQuiz()
        }
    }

    private fun startCountDown() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                updateCountDownText()
                checkAnswer()
            }
        }.start()
    }

    private fun updateCountDownText() {
        val minutes = (timeLeftInMillis / 1000).toInt() / 60
        val seconds = (timeLeftInMillis / 1000).toInt() % 60

        val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

        textViewCountDown!!.text = timeFormatted

        if (timeLeftInMillis < 10000) {
            textViewCountDown!!.setTextColor(Color.RED)
        } else {
            textViewCountDown!!.setTextColor(textColorDefaultCd)
        }
    }

    private fun checkAnswer() {
        answered = true

        countDownTimer!!.cancel()

        val rbSelected = findViewById<RadioButton>(rbGroup!!.checkedRadioButtonId)
        val answerNr = rbGroup!!.indexOfChild(rbSelected) + 1

        if (answerNr == currentQuestion!!.answerNr) {
            score++
            textViewScore!!.text = "Score: $score"
        }

        showSolution()
    }

    private fun showSolution() {
        rb1!!.setTextColor(Color.RED)
        rb2!!.setTextColor(Color.RED)
        rb3!!.setTextColor(Color.RED)

        when (currentQuestion!!.answerNr) {
            1 -> {
                rb1!!.setTextColor(Color.GREEN)
                textViewQuestion!!.text = "Answer 1 is correct"
            }
            2 -> {
                rb2!!.setTextColor(Color.GREEN)
                textViewQuestion!!.text = "Answer 2 is correct"
            }
            3 -> {
                rb3!!.setTextColor(Color.GREEN)
                textViewQuestion!!.text = "Answer 3 is correct"
            }
        }

        if (questionCounter < questionCountTotal) {
            buttonConfirmNext!!.text = "Next"
        } else {
            buttonConfirmNext!!.text = "Finish"
        }
    }

    private fun finishQuiz() {
        val resultIntent = Intent()
        resultIntent.putExtra(EXTRA_SCORE, score)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishQuiz()
        } else {
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show()
        }

        backPressedTime = System.currentTimeMillis()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
    }

    companion object {

        val EXTRA_SCORE = "extraScore"
        private val COUNTDOWN_IN_MILLIS: Long = 30000
    }
}


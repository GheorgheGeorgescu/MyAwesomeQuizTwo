package com.example.myawesomequiztwo

class Question {
    var question: String? = null
    var option1: String? = null
    var option2: String? = null
    var option3: String? = null
    var answerNr: Int = 0

    constructor() {}

    constructor(question: String, option1: String, option2: String, option3: String, answerNr: Int) {
        this.question = question
        this.option1 = option1
        this.option2 = option2
        this.option3 = option3
        this.answerNr = answerNr
    }
}

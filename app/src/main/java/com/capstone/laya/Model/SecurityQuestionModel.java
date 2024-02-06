package com.capstone.laya.Model;

public class SecurityQuestionModel {

    String Question, Answer, Id;

    public SecurityQuestionModel() {
    }
    public SecurityQuestionModel(String Question, String Answer, String Id) {
        this.Question = Question;
        this.Answer = Answer;
        this.Id = Id;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}

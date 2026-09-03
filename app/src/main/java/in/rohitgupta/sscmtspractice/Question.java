package in.rohitgupta.sscmtspractice;

import java.util.List;

public final class Question {
    private final String id;
    private final String subject;
    private final String topic;
    private final String text;
    private final List<String> options;
    private final int answerIndex;
    private final String explanation;

    public Question(String id, String subject, String topic, String text,
                    List<String> options, int answerIndex, String explanation) {
        this.id = id;
        this.subject = subject;
        this.topic = topic;
        this.text = text;
        this.options = options;
        this.answerIndex = answerIndex;
        this.explanation = explanation;
    }

    public String getId() { return id; }
    public String getSubject() { return subject; }
    public String getTopic() { return topic; }
    public String getText() { return text; }
    public List<String> getOptions() { return options; }
    public int getAnswerIndex() { return answerIndex; }
    public String getExplanation() { return explanation; }
}

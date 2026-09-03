package in.rohitgupta.sscmtspractice;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class QuestionRepository {
    private QuestionRepository() {}

    public static List<Question> load(Context context) {
        List<Question> questions = new ArrayList<>();
        try (InputStream input = context.getAssets().open("questions.json");
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int count;
            while ((count = input.read(buffer)) != -1) {
                output.write(buffer, 0, count);
            }
            JSONArray array = new JSONArray(output.toString(StandardCharsets.UTF_8.name()));
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                JSONArray sourceOptions = item.getJSONArray("options");
                List<String> options = new ArrayList<>();
                for (int j = 0; j < sourceOptions.length(); j++) {
                    options.add(sourceOptions.getString(j));
                }
                questions.add(new Question(
                        item.getString("id"),
                        item.getString("subject"),
                        item.getString("topic"),
                        item.getString("question"),
                        options,
                        item.getInt("answer"),
                        item.getString("explanation")
                ));
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Question bank could not be loaded", exception);
        }
        return questions;
    }

    public static List<Question> bySubject(List<Question> all, String subject) {
        List<Question> filtered = new ArrayList<>();
        for (Question question : all) {
            if (subject.equals(question.getSubject())) filtered.add(question);
        }
        return filtered;
    }
}

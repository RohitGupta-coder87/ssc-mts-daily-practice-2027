package in.rohitgupta.sscmtspractice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class MainActivity extends Activity {
    private static final int BG = Color.rgb(246, 247, 251);
    private static final int SURFACE = Color.WHITE;
    private static final int PRIMARY = Color.rgb(91, 33, 182);
    private static final int PRIMARY_DARK = Color.rgb(59, 7, 100);
    private static final int TEXT = Color.rgb(17, 24, 39);
    private static final int MUTED = Color.rgb(100, 116, 139);
    private static final int GREEN = Color.rgb(22, 163, 74);
    private static final int RED = Color.rgb(220, 38, 38);
    private static final int AMBER = Color.rgb(245, 158, 11);

    private FrameLayout root;
    private SharedPreferences prefs;
    private List<Question> allQuestions;
    private List<Question> quizQuestions = new ArrayList<>();
    private final List<Question> wrongQuestions = new ArrayList<>();
    private final AdsManager ads = new AdsManager();

    private String currentScreen = "HOME";
    private String quizMode = "";
    private int questionIndex;
    private int quizScore;
    private boolean answerLocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(PRIMARY_DARK);
        window.setNavigationBarColor(Color.WHITE);

        prefs = getSharedPreferences("practice_state", MODE_PRIVATE);
        allQuestions = QuestionRepository.load(this);
        root = new FrameLayout(this);
        root.setBackgroundColor(BG);
        setContentView(root);
        ads.initialize(this, () -> {
            if (!isFinishing() && "HOME".equals(currentScreen)) showHome();
        });
        showHome();
    }

    private void showHome() {
        currentScreen = "HOME";
        LinearLayout body = vertical();
        body.setPadding(dp(18), dp(14), dp(18), dp(30));

        LinearLayout hero = vertical();
        hero.setPadding(dp(20), dp(20), dp(20), dp(20));
        hero.setBackground(rounded(PRIMARY, 22, PRIMARY));
        TextView eyebrow = text("SSC MTS • TARGET 2027", 12, Color.rgb(221, 214, 254), true);
        hero.addView(eyebrow);
        hero.addView(space(8));
        TextView title = text("आज पढ़ो। आज ही\nअपना Score बढ़ाओ।", 25, Color.WHITE, true);
        title.setLineSpacing(dp(3), 1f);
        hero.addView(title);
        hero.addView(space(10));
        hero.addView(text("Original practice • Hindi explanations • Offline learning", 13, Color.rgb(237, 233, 254), false));
        hero.addView(space(16));
        TextView daily = action("आज का Daily Quiz शुरू करें  →", Color.WHITE, PRIMARY);
        daily.setOnClickListener(v -> startDailyQuiz());
        hero.addView(daily, matchWrap());
        body.addView(hero, matchWrap());

        body.addView(space(18));
        body.addView(sectionTitle("आपकी Progress", "हर practice attempt device में सुरक्षित रहता है"));
        body.addView(space(10));

        LinearLayout stats = horizontal();
        stats.setWeightSum(3f);
        stats.addView(statCard("🔥", String.valueOf(getStreak()), "दिन streak"), weighted());
        stats.addView(horizontalGap());
        stats.addView(statCard("✓", String.valueOf(prefs.getInt("quizzes", 0)), "quiz पूरे"), weighted());
        stats.addView(horizontalGap());
        int attempts = prefs.getInt("attempts", 0);
        int correct = prefs.getInt("correct", 0);
        String accuracy = attempts == 0 ? "0%" : Math.round(correct * 100f / attempts) + "%";
        stats.addView(statCard("◎", accuracy, "accuracy"), weighted());
        body.addView(stats, matchWrap());

        body.addView(space(22));
        body.addView(sectionTitle("Practice चुनें", "Subject-wise speed और accuracy मजबूत करें"));
        body.addView(space(10));
        body.addView(subjectRow("English", "Grammar & Vocabulary", "ENG", Color.rgb(37, 99, 235)));
        body.addView(space(10));
        body.addView(subjectRow("Mathematics", "Arithmetic & Calculation", "MATH", Color.rgb(234, 88, 12)));
        body.addView(space(10));
        body.addView(subjectRow("Reasoning", "Logic & Mental Ability", "REASONING", Color.rgb(5, 150, 105)));
        body.addView(space(10));
        body.addView(subjectRow("General Awareness", "Static GK & Science", "GK", Color.rgb(190, 24, 93)));

        body.addView(space(18));
        TextView mock = action("⚡  Full Mock Test • 40 Questions", PRIMARY, Color.WHITE);
        mock.setOnClickListener(v -> startQuiz(randomSelection(allQuestions, 40, System.currentTimeMillis()), "Full Mock Test"));
        body.addView(mock, matchWrap());

        body.addView(space(12));
        LinearLayout quick = horizontal();
        quick.setWeightSum(2f);
        TextView bookmarks = secondaryAction("★  Bookmarks");
        bookmarks.setOnClickListener(v -> showBookmarks());
        quick.addView(bookmarks, weightedHeight(54));
        quick.addView(horizontalGap());
        TextView history = secondaryAction("↻  History");
        history.setOnClickListener(v -> showHistory());
        quick.addView(history, weightedHeight(54));
        body.addView(quick, matchWrap());

        if (ads.isPrivacyOptionsRequired()) {
            body.addView(space(10));
            TextView privacy = secondaryAction("⚙  Advertising privacy choices");
            privacy.setOnClickListener(v -> ads.showPrivacyOptions(this));
            body.addView(privacy, matchWrap());
        }

        body.addView(space(20));
        LinearLayout note = vertical();
        note.setPadding(dp(16), dp(14), dp(16), dp(14));
        note.setBackground(rounded(Color.rgb(255, 251, 235), 16, Color.rgb(253, 230, 138)));
        note.addView(text("आज की Winning Strategy", 15, Color.rgb(146, 64, 14), true));
        note.addView(space(5));
        note.addView(text("पहले accuracy पर ध्यान दें। हर गलत प्रश्न का explanation पढ़कर bookmark करें।", 13, Color.rgb(120, 53, 15), false));
        body.addView(note, matchWrap());

        body.addView(space(22));
        TextView author = text("Designed for SSC aspirants • By Rohit Kumar Gupta", 12, MUTED, false);
        author.setGravity(Gravity.CENTER);
        body.addView(author, matchWrap());
        render(body, true);
    }

    private View subjectRow(String name, String subtitle, String subject, int color) {
        LinearLayout row = horizontal();
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(14), dp(14), dp(14), dp(14));
        row.setBackground(rounded(SURFACE, 18, Color.rgb(226, 232, 240)));

        TextView icon = text(name.substring(0, 1), 18, Color.WHITE, true);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(rounded(color, 14, color));
        row.addView(icon, new LinearLayout.LayoutParams(dp(48), dp(48)));

        LinearLayout copy = vertical();
        copy.setPadding(dp(12), 0, dp(8), 0);
        copy.addView(text(name, 16, TEXT, true));
        copy.addView(space(3));
        copy.addView(text(subtitle, 12, MUTED, false));
        row.addView(copy, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView arrow = text("›", 28, color, true);
        row.addView(arrow);
        row.setClickable(true);
        row.setFocusable(true);
        row.setContentDescription(name + " practice खोलें");
        row.setOnClickListener(v -> {
            List<Question> set = QuestionRepository.bySubject(allQuestions, subject);
            startQuiz(randomSelection(set, Math.min(15, set.size()), System.currentTimeMillis()), name + " Practice");
        });
        return row;
    }

    private void startDailyQuiz() {
        String today = dateKey(Calendar.getInstance());
        startQuiz(randomSelection(allQuestions, 25, today.hashCode()), "Daily Quiz");
    }

    private void startQuiz(List<Question> questions, String mode) {
        if (questions.isEmpty()) {
            Toast.makeText(this, "अभी इस section में प्रश्न उपलब्ध नहीं हैं।", Toast.LENGTH_SHORT).show();
            return;
        }
        quizQuestions = new ArrayList<>(questions);
        quizMode = mode;
        questionIndex = 0;
        quizScore = 0;
        answerLocked = false;
        wrongQuestions.clear();
        showQuestion();
    }

    private void showQuestion() {
        currentScreen = "QUIZ";
        Question question = quizQuestions.get(questionIndex);
        LinearLayout body = vertical();
        body.setPadding(dp(18), dp(12), dp(18), dp(26));
        body.addView(topBar("‹", quizMode, "बाहर जाएँ", v -> confirmExitQuiz()));
        body.addView(space(14));

        LinearLayout progressRow = horizontal();
        progressRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView progressCopy = text("Question " + (questionIndex + 1) + " of " + quizQuestions.size(), 13, MUTED, true);
        progressRow.addView(progressCopy, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        progressRow.addView(text("Score: " + quizScore, 13, GREEN, true));
        body.addView(progressRow, matchWrap());
        body.addView(space(8));
        ProgressBar progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(quizQuestions.size());
        progress.setProgress(questionIndex + 1);
        progress.getProgressDrawable().setTint(PRIMARY);
        body.addView(progress, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(7)));

        body.addView(space(18));
        LinearLayout card = vertical();
        card.setPadding(dp(18), dp(18), dp(18), dp(18));
        card.setBackground(rounded(SURFACE, 20, Color.rgb(226, 232, 240)));

        LinearLayout meta = horizontal();
        meta.setGravity(Gravity.CENTER_VERTICAL);
        TextView pill = text(question.getSubject() + "  •  " + question.getTopic(), 11, PRIMARY, true);
        pill.setPadding(dp(10), dp(6), dp(10), dp(6));
        pill.setBackground(rounded(Color.rgb(245, 243, 255), 99, Color.rgb(221, 214, 254)));
        meta.addView(pill, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView star = text(isBookmarked(question.getId()) ? "★" : "☆", 27, AMBER, true);
        star.setGravity(Gravity.CENTER);
        star.setContentDescription("प्रश्न bookmark करें");
        star.setOnClickListener(v -> {
            toggleBookmark(question.getId());
            star.setText(isBookmarked(question.getId()) ? "★" : "☆");
        });
        meta.addView(star, new LinearLayout.LayoutParams(dp(48), dp(44)));
        card.addView(meta);
        card.addView(space(16));

        TextView qText = text(question.getText(), 18, TEXT, true);
        qText.setLineSpacing(dp(4), 1f);
        card.addView(qText, matchWrap());
        card.addView(space(18));

        List<TextView> optionViews = new ArrayList<>();
        for (int i = 0; i < question.getOptions().size(); i++) {
            final int selected = i;
            TextView option = text(optionLabel(i) + "   " + question.getOptions().get(i), 15, TEXT, false);
            option.setGravity(Gravity.CENTER_VERTICAL);
            option.setPadding(dp(14), dp(13), dp(14), dp(13));
            option.setBackground(rounded(Color.rgb(248, 250, 252), 14, Color.rgb(203, 213, 225)));
            option.setClickable(true);
            option.setFocusable(true);
            option.setOnClickListener(v -> answer(question, selected, optionViews, body));
            optionViews.add(option);
            card.addView(option, matchWrap());
            card.addView(space(10));
        }
        body.addView(card, matchWrap());
        render(body, false);
    }

    private void answer(Question question, int selected, List<TextView> options, LinearLayout page) {
        if (answerLocked) return;
        answerLocked = true;
        boolean correct = selected == question.getAnswerIndex();
        if (correct) quizScore++; else wrongQuestions.add(question);

        for (int i = 0; i < options.size(); i++) {
            TextView option = options.get(i);
            option.setClickable(false);
            if (i == question.getAnswerIndex()) {
                option.setTextColor(Color.rgb(20, 83, 45));
                option.setBackground(rounded(Color.rgb(220, 252, 231), 14, Color.rgb(74, 222, 128)));
            } else if (i == selected) {
                option.setTextColor(Color.rgb(127, 29, 29));
                option.setBackground(rounded(Color.rgb(254, 226, 226), 14, Color.rgb(248, 113, 113)));
            } else {
                option.setAlpha(0.65f);
            }
        }

        LinearLayout explanation = vertical();
        explanation.setPadding(dp(16), dp(14), dp(16), dp(14));
        int boxColor = correct ? Color.rgb(240, 253, 244) : Color.rgb(255, 247, 237);
        int border = correct ? Color.rgb(134, 239, 172) : Color.rgb(253, 186, 116);
        explanation.setBackground(rounded(boxColor, 16, border));
        explanation.addView(text(correct ? "✓ सही उत्तर" : "✕ उत्तर गलत है", 15, correct ? GREEN : RED, true));
        explanation.addView(space(6));
        explanation.addView(text(question.getExplanation(), 14, TEXT, false));
        page.addView(space(14));
        page.addView(explanation, matchWrap());
        page.addView(space(12));

        String nextCopy = questionIndex == quizQuestions.size() - 1 ? "Result देखें  →" : "अगला प्रश्न  →";
        TextView next = action(nextCopy, PRIMARY, Color.WHITE);
        next.setOnClickListener(v -> {
            if (questionIndex == quizQuestions.size() - 1) finishQuiz();
            else {
                questionIndex++;
                answerLocked = false;
                showQuestion();
            }
        });
        page.addView(next, matchWrap());
    }

    private void finishQuiz() {
        int total = quizQuestions.size();
        int wrong = total - quizScore;
        int percent = Math.round(quizScore * 100f / total);
        prefs.edit()
                .putInt("quizzes", prefs.getInt("quizzes", 0) + 1)
                .putInt("attempts", prefs.getInt("attempts", 0) + total)
                .putInt("correct", prefs.getInt("correct", 0) + quizScore)
                .apply();
        addHistory(quizMode, quizScore, total);
        if ("Daily Quiz".equals(quizMode)) updateStreak();

        currentScreen = "RESULT";
        LinearLayout body = vertical();
        body.setPadding(dp(18), dp(18), dp(18), dp(30));
        body.addView(topBar("‹", "Quiz Result", "Home", v -> showHome()));
        body.addView(space(22));

        LinearLayout resultCard = vertical();
        resultCard.setGravity(Gravity.CENTER_HORIZONTAL);
        resultCard.setPadding(dp(22), dp(28), dp(22), dp(28));
        resultCard.setBackground(rounded(SURFACE, 22, Color.rgb(226, 232, 240)));
        TextView trophy = text(percent >= 80 ? "🏆" : percent >= 50 ? "💪" : "📚", 44, TEXT, false);
        resultCard.addView(trophy);
        resultCard.addView(space(8));
        resultCard.addView(text(percent >= 80 ? "शानदार प्रदर्शन!" : percent >= 50 ? "अच्छी कोशिश!" : "Practice जारी रखें!", 23, TEXT, true));
        resultCard.addView(space(6));
        resultCard.addView(text(quizMode, 13, MUTED, false));
        resultCard.addView(space(18));
        TextView scoreCircle = text(percent + "%", 36, PRIMARY, true);
        scoreCircle.setGravity(Gravity.CENTER);
        scoreCircle.setBackground(rounded(Color.rgb(245, 243, 255), 99, Color.rgb(196, 181, 253)));
        resultCard.addView(scoreCircle, new LinearLayout.LayoutParams(dp(112), dp(112)));
        resultCard.addView(space(18));

        LinearLayout split = horizontal();
        split.setWeightSum(2f);
        split.addView(resultMini("✓ " + quizScore, "Correct", GREEN), weighted());
        split.addView(horizontalGap());
        split.addView(resultMini("✕ " + wrong, "Incorrect", RED), weighted());
        resultCard.addView(split, matchWrap());
        body.addView(resultCard, matchWrap());

        body.addView(space(16));
        if (!wrongQuestions.isEmpty()) {
            TextView revise = secondaryAction("गलत प्रश्न Bookmark करें (" + wrongQuestions.size() + ")");
            revise.setOnClickListener(v -> {
                for (Question q : wrongQuestions) setBookmark(q.getId(), true);
                Toast.makeText(this, "Revision के लिए प्रश्न save हो गए।", Toast.LENGTH_SHORT).show();
                revise.setText("✓ Bookmarks में save हो गए");
                revise.setEnabled(false);
            });
            body.addView(revise, matchWrap());
            body.addView(space(10));
        }
        TextView retry = action("फिर से Practice करें", PRIMARY, Color.WHITE);
        retry.setOnClickListener(v -> startQuiz(randomSelection(quizQuestions, quizQuestions.size(), System.currentTimeMillis()), quizMode));
        body.addView(retry, matchWrap());
        body.addView(space(10));
        TextView home = secondaryAction("Home पर जाएँ");
        home.setOnClickListener(v -> showHome());
        body.addView(home, matchWrap());
        render(body, true);
    }

    private void showBookmarks() {
        currentScreen = "BOOKMARKS";
        Set<String> saved = bookmarkIds();
        List<Question> questions = new ArrayList<>();
        for (Question q : allQuestions) if (saved.contains(q.getId())) questions.add(q);

        LinearLayout body = vertical();
        body.setPadding(dp(18), dp(12), dp(18), dp(28));
        body.addView(topBar("‹", "Bookmarked Questions", "Home", v -> showHome()));
        body.addView(space(18));
        if (questions.isEmpty()) {
            body.addView(emptyState("☆", "कोई Bookmark नहीं", "Quiz में star दबाकर कठिन प्रश्न यहाँ सुरक्षित करें।"));
        } else {
            body.addView(text(questions.size() + " प्रश्न revision के लिए तैयार हैं।", 14, MUTED, false));
            body.addView(space(12));
            TextView start = action("Bookmarks Practice शुरू करें", PRIMARY, Color.WHITE);
            start.setOnClickListener(v -> startQuiz(randomSelection(questions, questions.size(), System.currentTimeMillis()), "Bookmark Revision"));
            body.addView(start, matchWrap());
            body.addView(space(14));
            for (Question q : questions) {
                LinearLayout card = vertical();
                card.setPadding(dp(14), dp(14), dp(14), dp(14));
                card.setBackground(rounded(SURFACE, 16, Color.rgb(226, 232, 240)));
                card.addView(text(q.getSubject() + " • " + q.getTopic(), 11, PRIMARY, true));
                card.addView(space(6));
                card.addView(text(q.getText(), 14, TEXT, true));
                body.addView(card, matchWrap());
                body.addView(space(10));
            }
        }
        render(body, false);
    }

    private void showHistory() {
        currentScreen = "HISTORY";
        LinearLayout body = vertical();
        body.setPadding(dp(18), dp(12), dp(18), dp(28));
        body.addView(topBar("‹", "Practice History", "Home", v -> showHome()));
        body.addView(space(18));
        String raw = prefs.getString("history", "");
        if (raw == null || raw.trim().isEmpty()) {
            body.addView(emptyState("↻", "History खाली है", "अपना पहला quiz पूरा करें और progress यहाँ देखें।"));
        } else {
            String[] rows = raw.split("\\n");
            for (String row : rows) {
                String[] values = row.split("\\|", -1);
                if (values.length != 4) continue;
                LinearLayout card = horizontal();
                card.setGravity(Gravity.CENTER_VERTICAL);
                card.setPadding(dp(14), dp(14), dp(14), dp(14));
                card.setBackground(rounded(SURFACE, 16, Color.rgb(226, 232, 240)));
                LinearLayout copy = vertical();
                copy.addView(text(values[1], 15, TEXT, true));
                copy.addView(space(4));
                copy.addView(text(values[0], 12, MUTED, false));
                card.addView(copy, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                int scored = Integer.parseInt(values[2]);
                int total = Integer.parseInt(values[3]);
                int pct = Math.round(scored * 100f / total);
                TextView badge = text(scored + "/" + total + "\n" + pct + "%", 14, pct >= 60 ? GREEN : RED, true);
                badge.setGravity(Gravity.CENTER);
                card.addView(badge, new LinearLayout.LayoutParams(dp(66), dp(54)));
                body.addView(card, matchWrap());
                body.addView(space(10));
            }
        }
        render(body, false);
    }

    private View topBar(String left, String title, String description, View.OnClickListener click) {
        LinearLayout bar = horizontal();
        bar.setGravity(Gravity.CENTER_VERTICAL);
        TextView back = text(left, 34, PRIMARY, false);
        back.setGravity(Gravity.CENTER);
        back.setContentDescription(description);
        back.setOnClickListener(click);
        bar.addView(back, new LinearLayout.LayoutParams(dp(48), dp(48)));
        TextView heading = text(title, 18, TEXT, true);
        heading.setGravity(Gravity.CENTER);
        bar.addView(heading, new LinearLayout.LayoutParams(0, dp(48), 1f));
        Space balance = new Space(this);
        bar.addView(balance, new LinearLayout.LayoutParams(dp(48), dp(48)));
        return bar;
    }

    private View sectionTitle(String title, String subtitle) {
        LinearLayout section = vertical();
        section.addView(text(title, 18, TEXT, true));
        section.addView(space(4));
        section.addView(text(subtitle, 12, MUTED, false));
        return section;
    }

    private View statCard(String icon, String value, String label) {
        LinearLayout card = vertical();
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.setPadding(dp(8), dp(12), dp(8), dp(12));
        card.setBackground(rounded(SURFACE, 16, Color.rgb(226, 232, 240)));
        card.addView(text(icon, 18, AMBER, true));
        card.addView(space(5));
        card.addView(text(value, 18, TEXT, true));
        card.addView(text(label, 10, MUTED, false));
        return card;
    }

    private View resultMini(String value, String label, int color) {
        LinearLayout box = vertical();
        box.setGravity(Gravity.CENTER_HORIZONTAL);
        box.setPadding(dp(8), dp(10), dp(8), dp(10));
        box.setBackground(rounded(Color.rgb(248, 250, 252), 14, Color.rgb(226, 232, 240)));
        box.addView(text(value, 18, color, true));
        box.addView(text(label, 11, MUTED, false));
        return box;
    }

    private View emptyState(String icon, String title, String subtitle) {
        LinearLayout state = vertical();
        state.setGravity(Gravity.CENTER_HORIZONTAL);
        state.setPadding(dp(24), dp(54), dp(24), dp(54));
        state.setBackground(rounded(SURFACE, 20, Color.rgb(226, 232, 240)));
        state.addView(text(icon, 44, PRIMARY, false));
        state.addView(space(10));
        state.addView(text(title, 20, TEXT, true));
        state.addView(space(6));
        TextView sub = text(subtitle, 14, MUTED, false);
        sub.setGravity(Gravity.CENTER);
        state.addView(sub);
        return state;
    }

    private void render(LinearLayout body, boolean showAd) {
        ads.destroy();
        root.removeAllViews();
        LinearLayout shell = vertical();
        shell.setBackgroundColor(BG);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.addView(body, matchWrap());
        shell.addView(scroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        if (showAd) ads.attachTestBanner(this, shell);
        root.addView(shell, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private List<Question> randomSelection(List<Question> source, int count, long seed) {
        List<Question> copy = new ArrayList<>(source);
        Collections.shuffle(copy, new Random(seed));
        return new ArrayList<>(copy.subList(0, Math.min(count, copy.size())));
    }

    private void toggleBookmark(String id) {
        setBookmark(id, !isBookmarked(id));
    }

    private boolean isBookmarked(String id) {
        return bookmarkIds().contains(id);
    }

    private Set<String> bookmarkIds() {
        return new HashSet<>(prefs.getStringSet("bookmarks", new HashSet<>()));
    }

    private void setBookmark(String id, boolean save) {
        Set<String> bookmarks = bookmarkIds();
        if (save) bookmarks.add(id); else bookmarks.remove(id);
        prefs.edit().putStringSet("bookmarks", bookmarks).apply();
    }

    private void addHistory(String mode, int score, int total) {
        String date = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Calendar.getInstance().getTime());
        String item = date + "|" + mode.replace("|", "") + "|" + score + "|" + total;
        String old = prefs.getString("history", "");
        String combined = item + ((old == null || old.isEmpty()) ? "" : "\n" + old);
        String[] rows = combined.split("\\n");
        StringBuilder trimmed = new StringBuilder();
        for (int i = 0; i < Math.min(20, rows.length); i++) {
            if (i > 0) trimmed.append('\n');
            trimmed.append(rows[i]);
        }
        prefs.edit().putString("history", trimmed.toString()).apply();
    }

    private void updateStreak() {
        Calendar now = Calendar.getInstance();
        String today = dateKey(now);
        String last = prefs.getString("last_daily", "");
        if (today.equals(last)) return;

        now.add(Calendar.DAY_OF_YEAR, -1);
        String yesterday = dateKey(now);
        int streak = yesterday.equals(last) ? prefs.getInt("streak", 0) + 1 : 1;
        prefs.edit().putString("last_daily", today).putInt("streak", streak).apply();
    }

    private int getStreak() { return prefs.getInt("streak", 0); }

    private String dateKey(Calendar calendar) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime());
    }

    private void confirmExitQuiz() {
        new AlertDialog.Builder(this)
                .setTitle("Quiz छोड़ना है?")
                .setMessage("इस attempt का score save नहीं होगा।")
                .setNegativeButton("Practice जारी रखें", null)
                .setPositiveButton("बाहर जाएँ", (dialog, which) -> showHome())
                .show();
    }

    @Override
    public void onBackPressed() {
        if ("QUIZ".equals(currentScreen)) confirmExitQuiz();
        else if (!"HOME".equals(currentScreen)) showHome();
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        ads.destroy();
        super.onDestroy();
    }

    private LinearLayout vertical() {
        LinearLayout view = new LinearLayout(this);
        view.setOrientation(LinearLayout.VERTICAL);
        return view;
    }

    private LinearLayout horizontal() {
        LinearLayout view = new LinearLayout(this);
        view.setOrientation(LinearLayout.HORIZONTAL);
        return view;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setTypeface(Typeface.create("sans", bold ? Typeface.BOLD : Typeface.NORMAL));
        view.setIncludeFontPadding(false);
        return view;
    }

    private TextView action(String value, int background, int foreground) {
        TextView view = text(value, 15, foreground, true);
        view.setGravity(Gravity.CENTER);
        view.setPadding(dp(14), dp(15), dp(14), dp(15));
        view.setBackground(rounded(background, 15, background));
        view.setClickable(true);
        view.setFocusable(true);
        return view;
    }

    private TextView secondaryAction(String value) {
        TextView view = text(value, 14, PRIMARY, true);
        view.setGravity(Gravity.CENTER);
        view.setPadding(dp(10), dp(14), dp(10), dp(14));
        view.setBackground(rounded(SURFACE, 15, Color.rgb(196, 181, 253)));
        view.setClickable(true);
        view.setFocusable(true);
        return view;
    }

    private GradientDrawable rounded(int fill, int radiusDp, int stroke) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(fill);
        shape.setCornerRadius(dp(radiusDp));
        shape.setStroke(dp(1), stroke);
        return shape;
    }

    private View space(int height) {
        Space space = new Space(this);
        space.setLayoutParams(new LinearLayout.LayoutParams(1, dp(height)));
        return space;
    }

    private View horizontalGap() {
        Space gap = new Space(this);
        gap.setLayoutParams(new LinearLayout.LayoutParams(dp(8), 1));
        return gap;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout.LayoutParams weighted() {
        return new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
    }

    private LinearLayout.LayoutParams weightedHeight(int height) {
        return new LinearLayout.LayoutParams(0, dp(height), 1f);
    }

    private String optionLabel(int index) {
        return String.valueOf((char) ('A' + index)) + ".";
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}

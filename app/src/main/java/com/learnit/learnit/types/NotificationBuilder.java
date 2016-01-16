package com.learnit.learnit.types;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.learnit.learnit.R;
import com.learnit.learnit.activities.HomeworkActivity;
import com.learnit.learnit.db_handlers.DbHandler;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotificationBuilder {
    public static final String LOG_TAG = "my_logs";

    public static final String IDS_TAG = "ids";
    public static final String WORDS_TAG = "words";
    public static final String ARTICLES_TAG = "articles";
    public static final String PREFIXES_TAG = "prefixes";
    public static final String TRANSLATIONS_TAG = "translations";
    public static final String HOMEWORK_TYPE_TAG = "homework_type";
    public static final String DIRECTIONS_OF_TRANS_TAG = "directions_of_trans";
    public static final String CURRENT_NOTIFICATION_INDEX = "current_index";

    static String currentIds = "";

    public static final int mIdStartingValue = 1552235; // some number


    private static void deleteOldNotifications(Context context, String old_ids) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (null != old_ids) {
            String[] ids = old_ids.split(" ");
            for (String id : ids) {
                if (null != id && !id.equals("")) {
                    mNotificationManager.cancel(Integer.parseInt(id));
                }
            }
        }
    }


    public static void show(Context context) {
        Log.d(LOG_TAG, "context class = " + context.getClass().getName());
        String old_ids = Prefs.getString("current_ids", "");
        deleteOldNotifications(context, old_ids);
        Constants.LearnType wayToLearn = getWayToLearn(context);
        Log.d(Constants.LOG_TAG, "got learn type: " + wayToLearn.name());
        int numberOfWords = setNumberOfWords(context);
        Log.d(LOG_TAG, "number of notifications = " + numberOfWords);

        DbHandler dbHandler = DbHandler.Factory.createLocalizedHelper(context, DbHandler.DB_USER_DICT);
        if (dbHandler == null) {
            Log.e(Constants.LOG_TAG, "cannot build notification. No connection to database");
            return;
        }
        List<WordBundle> randWords = dbHandler.queryRandomWords(4);
        CreateNotifications(randWords, context, wayToLearn);
        Prefs.putString("current_ids", currentIds);
    }

    private static Constants.LearnType getHomeworkType() {
         return Constants.LearnType.TRANSLATIONS;
    }

    private static int setNumberOfWords(Context context) {
        return Prefs.getInt(context.getString(R.string.key_num_of_words), 9);
    }

    private static Constants.DirectionOfTranslation getDirectionOfTranslation(
            Context context,
            Constants.LearnType homeworkActivityType) {
        if (homeworkActivityType==Constants.LearnType.ARTICLES) {
            return Constants.DirectionOfTranslation.NEW_TO_KNOWN;
        }
        int currentDirectionKey = Prefs.getInt(context.getString(R.string.key_direction_of_trans), 2);
        Constants.DirectionOfTranslation directionOfTranslation = Utils.enumValueFromKey(
                currentDirectionKey, Constants.DirectionOfTranslation.class);
        if (directionOfTranslation == Constants.DirectionOfTranslation.MIXED) {
            Random rand = new Random();
            currentDirectionKey = rand.nextInt(2);
            directionOfTranslation = Utils.enumValueFromKey(
                    currentDirectionKey, Constants.DirectionOfTranslation.class);
        }
        return directionOfTranslation;
    }

    private static Constants.LearnType getWayToLearn(Context context) {
        int wayToLearn = Prefs.getInt(context.getString(R.string.key_way_to_learn), 3);
        return Utils.enumValueFromKey(wayToLearn, Constants.LearnType.class);
    }

    private static NotificationCompat.Builder getBuilder(
            Context context,
            Constants.DirectionOfTranslation translationDirection,
            WordBundle wordBundle)
    {
        switch (translationDirection) {
            case NEW_TO_KNOWN:
                return new NotificationCompat.Builder(context)
                        .setContentTitle(wordBundle.word())
                        .setContentText(context.getString(R.string.notification_text));
            case KNOWN_TO_NEW:
                return new NotificationCompat.Builder(context)
                        .setContentTitle(wordBundle.transAsHumanString())
                        .setContentText(context.getString(R.string.notification_text));
        }
        return null;
    }


    private static boolean CreateNotifications(
            List<WordBundle> randWords,
            Context context,
            Constants.LearnType wayToLearn) {
        ArrayList<Intent> intents = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<String> words = new ArrayList<>();
        ArrayList<String> articles = new ArrayList<>();
        ArrayList<String> translations = new ArrayList<>();
        ArrayList<String> prefixes = new ArrayList<>();
        ArrayList<Integer> directionsOfTrans = new ArrayList<>();
        ArrayList<Integer> typesOfHomework = new ArrayList<>();
        ArrayList<Class> classes = new ArrayList<>();
        for (WordBundle wordBundle: randWords)
        {
            Constants.LearnType homeworkActivityType = getHomeworkType();
            Constants.DirectionOfTranslation directionOfTranslation = getDirectionOfTranslation(
                    context, homeworkActivityType);
            ids.add(wordBundle.id() + mIdStartingValue);
            words.add(wordBundle.word());
            articles.add(wordBundle.article());
            translations.add(wordBundle.transAsHumanString());
            prefixes.add(wordBundle.prefix());
            intents.add(new Intent(context, HomeworkActivity.class));
            classes.add(HomeworkActivity.class);

            directionsOfTrans.add(Utils.keyFromEnumValue(
                    directionOfTranslation, Constants.DirectionOfTranslation.class));
            typesOfHomework.add(Utils.keyFromEnumValue(
                    homeworkActivityType, Constants.LearnType.class));
        }
        for (int i=0; i<intents.size(); ++i)
        {
            Intent intent = intents.get(i);
            intent.putExtra(IDS_TAG, ids);
            intent.putExtra(WORDS_TAG, words);
            intent.putExtra(TRANSLATIONS_TAG, translations);
            intent.putExtra(ARTICLES_TAG, articles);
            intent.putExtra(PREFIXES_TAG, prefixes);
            intent.putExtra(DIRECTIONS_OF_TRANS_TAG, directionsOfTrans);
            intent.putExtra(HOMEWORK_TYPE_TAG, typesOfHomework);
            intent.putExtra(CURRENT_NOTIFICATION_INDEX, i);
            intent.setAction(ids.get(i) + " " + words.get(i) + " " + System.currentTimeMillis());
            NotificationCompat.Builder mBuilder;
            mBuilder = getBuilder(
                    context,
                    Utils.enumValueFromKey(directionsOfTrans.get(i), Constants.DirectionOfTranslation.class),
                    randWords.get(i));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            stackBuilder.addParentStack(classes.get(i));
            stackBuilder.addNextIntent(intent);
            PendingIntent pendInt = PendingIntent.getActivity(context, ids.get(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (null != mBuilder)
            {
                mBuilder.setSmallIcon(Utils.getIconForWordNumber(i+1));
                mBuilder.setContentIntent(pendInt);
                mBuilder.setPriority(Notification.PRIORITY_MAX);
                mBuilder.setOngoing(true);
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(ids.get(i), mBuilder.build());
                currentIds = currentIds + ids.get(i) + " ";
            }
            else
                return false;
        }
        return true;
    }
}
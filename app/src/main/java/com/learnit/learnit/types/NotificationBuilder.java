package com.learnit.learnit.types;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

    public static final String WORDS_TAG = "words";
    public static final String HOMEWORK_TYPE_TAG = "homework_type";
    public static final String DIRECTIONS_OF_TRANS_TAG = "directions_of_trans";
    public static final String CURRENT_NOTIFICATION_INDEX_TAG = "notification_index";

    public static final String CURRENT_IDS_TAG = "current_ids";

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

    public static int notificationIdFromWordId(final int wordId) {
        return mIdStartingValue + wordId;
    }


    public static void show(Context context) {
        Log.d(LOG_TAG, "context class = " + context.getClass().getName());
        String old_ids = Prefs.getString(CURRENT_IDS_TAG, "");
        deleteOldNotifications(context, old_ids);
        Constants.LearnType wayToLearn = getWayToLearn(context);
        Log.d(Constants.LOG_TAG, "got learn type: " + wayToLearn.name());
        int numberOfWords = getNumberOfWords(context);
        Log.d(LOG_TAG, "number of notifications = " + numberOfWords);

        DbHandler dbHandler = DbHandler.Factory.createLocalizedHelper(context, DbHandler.DB_USER_DICT);
        if (dbHandler == null) {
            Log.e(Constants.LOG_TAG, "cannot build notification. No connection to database");
            return;
        }
        List<WordBundle> randWords = dbHandler.queryRandomWords(numberOfWords, null);
        CreateNotifications(randWords, context, wayToLearn);
        Prefs.putString(CURRENT_IDS_TAG, currentIds);
    }

    private static Constants.LearnType getHomeworkType() {
         return Constants.LearnType.TRANSLATIONS;
    }

    private static int getNumberOfWords(Context context) {
        return Prefs.getInt(context.getString(R.string.key_num_of_words), 9);
    }

    private static Constants.DirectionOfTranslation getDirectionOfTranslation(
            Context context,
            Constants.LearnType homeworkActivityType) {
        if (homeworkActivityType==Constants.LearnType.ARTICLES) {
            return Constants.DirectionOfTranslation.NEW_TO_KNOWN;
        }
        int currentDirectionValueIndex = Prefs.getInt(context.getString(R.string.key_direction_of_trans), 2);
        Constants.DirectionOfTranslation directionOfTranslation = Utils.enumValueFromKey(
                currentDirectionValueIndex, Constants.DirectionOfTranslation.class);
        if (directionOfTranslation == Constants.DirectionOfTranslation.MIXED) {
            Random rand = new Random();
            currentDirectionValueIndex = rand.nextInt(2);
            directionOfTranslation = Utils.enumValueFromKey(
                    currentDirectionValueIndex, Constants.DirectionOfTranslation.class);
        }
        return directionOfTranslation;
    }

    private static Constants.LearnType getWayToLearn(Context context) {
        int wayToLearn = Prefs.getInt(context.getString(R.string.key_way_to_learn), 2);
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
        ArrayList<WordBundle> words = new ArrayList<>();
        ArrayList<Integer> directionsOfTrans = new ArrayList<>();
        ArrayList<Integer> typesOfHomework = new ArrayList<>();
        for (WordBundle wordBundle: randWords) {
            Constants.LearnType homeworkActivityType = getHomeworkType();
            Constants.DirectionOfTranslation directionOfTranslation = getDirectionOfTranslation(
                    context, homeworkActivityType);
            words.add(wordBundle);
            intents.add(new Intent(context, HomeworkActivity.class));

            directionsOfTrans.add(Utils.keyFromEnumValue(
                    directionOfTranslation, Constants.DirectionOfTranslation.class));
            typesOfHomework.add(Utils.keyFromEnumValue(
                    homeworkActivityType, Constants.LearnType.class));
        }
        NotificationManager mNotificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        for (int i = intents.size() - 1; i >=0; --i) {
            Intent intent = intents.get(i);
            intent.putParcelableArrayListExtra(WORDS_TAG, words);
            intent.putExtra(DIRECTIONS_OF_TRANS_TAG, directionsOfTrans);
            intent.putExtra(HOMEWORK_TYPE_TAG, typesOfHomework);
            intent.putExtra(CURRENT_NOTIFICATION_INDEX_TAG, i);
            intent.setAction(notificationIdFromWordId(words.get(i).id())
                    + " " + words.get(i)
                    + " " + System.currentTimeMillis());
            Constants.DirectionOfTranslation directionOfTranslation = Utils.enumValueFromKey(
                    directionsOfTrans.get(i), Constants.DirectionOfTranslation.class);
            NotificationCompat.Builder mBuilder = getBuilder(
                    context, directionOfTranslation, randWords.get(i));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            stackBuilder.addNextIntent(intent);
            PendingIntent pendInt = PendingIntent.getActivity(
                    context,
                    notificationIdFromWordId(words.get(i).id()),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            if (null != mBuilder)
            {
                mBuilder.setSmallIcon(Utils.getIconForWordNumber(i+1));
                mBuilder.setContentIntent(pendInt);
                mBuilder.setPriority(Notification.PRIORITY_MAX);
                mBuilder.setOngoing(true);
                mNotificationManager.notify(notificationIdFromWordId(words.get(i).id()), mBuilder.build());
                currentIds = currentIds + notificationIdFromWordId(words.get(i).id()) + " ";
            }
            else
                return false;
        }
        return true;
    }
}
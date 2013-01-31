package com.ago.guitartrainer.db;

import com.ago.guitartrainer.lessons.QuestionMetrics;
import com.ago.guitartrainer.lessons.custom.QuestionPosition2Note;
import com.ago.guitartrainer.lessons.custom.QuestionScalegridDegree2Position;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    public static final Class<?>[] classes = new Class[] { QuestionScalegridDegree2Position.class, QuestionMetrics.class, QuestionPosition2Note.class};

    public static void main(String[] args) throws Exception {
        writeConfigFile("ormlite_config.txt", classes);
    }
}
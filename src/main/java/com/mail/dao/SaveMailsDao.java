package com.mail.dao;

import com.mail.com.mail.utils.LpMailStatus;
import com.mail.structure.LpMail;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.Date;

public class SaveMailsDao {
    private static SaveMailsDao INSTANCE;
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection table;

    private SaveMailsDao() {
        mongoClient = new MongoClient("localhost", 21017);
        database = mongoClient.getDatabase("LPMailDB");
        table = database.getCollection("LPMailTable");
    }

    public static SaveMailsDao getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SaveMailsDao();
        }
        return INSTANCE;
    }

    public static void saveToMongoDb(LpMail lpMail, LpMailStatus status) {
        BasicDBObject document = new BasicDBObject();
        document.put("from", lpMail.getSenderEmail());
        document.put("to", lpMail.getRecipientEmail());
        document.put("date", new Date());
        document.put("status", status.toString());
        table.insertOne(document);
    }

    public static boolean verifyDBEntryExists(LpMail mail) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("to", mail.getRecipientEmail());
        FindIterable iterable = table.find(searchQuery);
        int  i = 0;
        while (iterable.iterator().hasNext()) {
                System.out.println("FOUND ON MONDO_DB: " + iterable.iterator().next());
                i++;
        }
        return i > 0;
    }
}

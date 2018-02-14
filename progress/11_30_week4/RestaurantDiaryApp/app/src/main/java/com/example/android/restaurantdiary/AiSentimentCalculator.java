package com.example.android.restaurantdiary;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentOptions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jake on 11/26/2017.
 */

public class AiSentimentCalculator {

    private static final String USERNAME = "9aff14e2-5530-40e8-98f9-fdb8a25e0e7c";
    private static final String PASSWORD = "AZeTU8O1UOSt";

    /**
     * AiSentiment method.
     *
     * @param textsToAnalyse the note/text to be analyzed by the ai
     *
     * @return Double which is the score -1 to 1 negative to positive sentiment
     */
    public static Double AiSentiment(String textsToAnalyse){

        if(textsToAnalyse.isEmpty() || textsToAnalyse == null){
            return 0.0;
        }

        try {
            NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
                    NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
                    USERNAME,
                    PASSWORD
            );

            String text = textsToAnalyse;

            SentimentOptions sentiment = new SentimentOptions.Builder().build();
            Features features = new Features.Builder().sentiment(sentiment).build();
            AnalyzeOptions parameters = new AnalyzeOptions.Builder().text(text).features(features).build();
            AnalysisResults response = service.analyze(parameters).execute();

            JSONObject test = new JSONObject();
            try {
                test = new JSONObject(response.toString());
            } catch (JSONException e) {
                return 0.0;
            }

            Double sentimentScore = new Double(0.0);
            try {
                sentimentScore = test.getJSONObject("sentiment").getJSONObject("document").getDouble("score");
            } catch (JSONException e) {
                return 0.0;
            }

            return sentimentScore;

        } catch (Exception e){
            return 0.0;
        }
    }
}

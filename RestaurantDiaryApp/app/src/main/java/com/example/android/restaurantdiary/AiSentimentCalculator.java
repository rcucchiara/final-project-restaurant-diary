package com.example.android.restaurantdiary;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentOptions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Calculates the AiSentiment using the Watson API.
 */

public class AiSentimentCalculator {

    /** Neutral Value for Watson Sentiment Score */
    private static final double NEUTRAL_SCORE = 0.0;

    /** String sentiment */
    private static final String SENTIMENT = "sentiment";

    /** String document */
    private static final String DOCUMENT = "document";

    /** String score */
    private static final String SCORE = "score";

    /** Username for Watson API credentials */
    private static final String USERNAME = "9aff14e2-5530-40e8-98f9-fdb8a25e0e7c";

    /** Password for Watson API credentials */
    private static final String PASSWORD = "AZeTU8O1UOSt";

    /** Watson service API object */
    private static final NaturalLanguageUnderstanding naturalLanguageUnderstanding =
            new NaturalLanguageUnderstanding(NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
                    USERNAME, PASSWORD);

    /** Sentiment options object */
    private static final SentimentOptions sentiment = new SentimentOptions.Builder().build();

    /** Features object */
    private static final Features features = new Features.Builder().sentiment(sentiment).build();

    /**
     * Analyzes the user note and passes back a sentiment score.
     *
     * @param textsToAnalyse the note/text to be analyzed by the ai
     *
     * @return Double which is the score -1 to 1 negative to positive sentiment
     */
    public static Double AiSentiment(String textsToAnalyse){

        if(textsToAnalyse.isEmpty() || textsToAnalyse == null){
            return NEUTRAL_SCORE;
        }

        AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                .text(textsToAnalyse).features(features).build();

        try {
            AnalysisResults response = naturalLanguageUnderstanding.analyze(parameters).execute();

            JSONObject responseInJSON = new JSONObject(response.toString());

            return responseInJSON.getJSONObject(SENTIMENT).getJSONObject(DOCUMENT)
                    .getDouble(SCORE);

        } catch (Exception e){
            return NEUTRAL_SCORE;
        }
    }
}

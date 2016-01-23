import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;

import java.io.File;
import java.util.List;

/**
 * Created by Rahul on 1/23/2016.
 */
public class FoodifaiCaller
{
    private static String APP_ID = "yzSmx6Vood4Sej97eqTsCHY5rkU-pK4qS7eQVDWR";
    private static String APP_SECRET = "uR-JBcoi8b5ti-oKeRad4AwgDwm0eQklZWOBOIsK";

    public static String[] getTags(String path)
    {
        ClarifaiClient clarifai = new ClarifaiClient(APP_ID, APP_SECRET);
        List<RecognitionResult> results =
                clarifai.recognize(new RecognitionRequest(new File("kittens.jpg")));

        for (Tag tag : results.get(0).getTags())
        {
            System.out.println(tag.getName() + ": " + tag.getProbability());
        }
    }
}

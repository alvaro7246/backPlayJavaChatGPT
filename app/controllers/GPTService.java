package services;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import play.Logger;
import play.libs.Json;

import javax.inject.Inject;
import java.io.IOException;

public class GPTService {

    private final String apiKey;

    @Inject
    //public GPTService() {this.apiKey = "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";} //API_KEY SIN CREDITO
    public GPTService() {this.apiKey = "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"; } //API_KEY CON CREDITO
    public String generarTexto(String prompt, int max_tokens) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.openai.com/v1/engines/davinci/completions");
        httpPost.addHeader("Authorization", "Bearer " + apiKey);
        httpPost.addHeader("Content-Type", "application/json");
        String jsonBody = Json.stringify(Json.newObject()
            .put("prompt", prompt)
        );

        httpPost.setEntity(new StringEntity(jsonBody, "UTF-8"));
        HttpResponse response = httpClient.execute(httpPost);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        Logger.info("Respuesta de GPT-3: " + responseBody);

        return responseBody;
    }

    public String generarTexto2(String prompt, int max_tokens) throws IOException {
        OpenAiService service = new OpenAiService(apiKey);
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model("ada")
                .maxTokens(max_tokens)
                .temperature(0.75)
                .build();
        System.out.println(service.createCompletion(completionRequest).getChoices());

        //Logger.info("Respuesta de GPT-3: " + responseBody);

        return "Exito";
    }
}
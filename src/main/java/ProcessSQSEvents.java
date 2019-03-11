import com.amazonaws.AmazonClientException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import com.google.gson.Gson;

public class ProcessSQSEvents implements RequestHandler<SQSEvent, Void>{
    /**
     * This is the handler that triggers when a new message is published to the
     * SQS queue topic this lambda is subscribed to.
     * @param event We set the batch to 1, so we can safely assume it's a list with
     *              one element: the message request containing the json parameteres.
     * @param context
     * @return
     */
    @Override
    public Void handleRequest(SQSEvent event, Context context)
    {
        String input = event.getRecords().get(0).getBody();
        context.getLogger().log("Input: " + input);

        Gson gson = new Gson();
        String json = gson.toJson(input);

        publishToSQS(MatchingAlgorithm.match(json));

        return null;
    }

    /**
     * APACHE 2.0 Liscense
     * Credits to : https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/standard-queues-getting-started-java.html
     */
    private void publishToSQS(String resultJson) {
        /*
         * Create a new instance of the builder with all defaults (credentials
         * and region) set automatically. For more information, see
         * Creating Service Clients in the AWS SDK for Java Developer Guide.
         */
        final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        System.out.println("===============================================");
        System.out.println("Getting Started with Amazon SQS Standard Queues");
        System.out.println("===============================================\n");

        try {
            // Create a queue.
            System.out.println("Creating a new SQS queue called MyQueue.\n");
            final CreateQueueRequest createQueueRequest =
                    new CreateQueueRequest("MyQueue");
            final String myQueueUrl = sqs.createQueue(createQueueRequest)
                    .getQueueUrl();

            // Send a message.
            System.out.println("Sending a message to MyQueue.\n");
            sqs.sendMessage(new SendMessageRequest(myQueueUrl,
                    resultJson));

        } catch (final AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means " +
                    "the client encountered a serious internal problem while " +
                    "trying to communicate with Amazon SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
}
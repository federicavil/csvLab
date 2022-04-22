import java.io.IOException;
import java.text.ParseException;

public class MainClass {

    public static void main(String[] args){
        Retriever ret = new Retriever("AVRO","C:/Users/Federica/git/avro", "HEAD");
        try {
            //ret.retrieveReleases();
            ret.retrieveBugs();
            //ret.retrieveCommits();
        } catch (IOException e) {
            e.printStackTrace();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

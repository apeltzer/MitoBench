import io.reader.MultiFastAInput;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by peltzer on 18/11/2016.
 */
public class IOTests {
    private InputStream is;
    private InputStreamReader isr;
    private BufferedReader bfr;


    private void setUp(String path){
        URL url = getClass().getResource(path);
        try {
            is = url.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isr = new InputStreamReader(is);
        bfr = new BufferedReader(isr);
    }


    @Test
    public void io_test_fasta(){
        String path = "./mFasta.fasta";
        setUp(path);
        HashMap output = null;
        try {
            MultiFastAInput multiFastAInput = new MultiFastAInput(getClass().getResource(path).getPath());
            output = multiFastAInput.getCorrespondingData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Data from Pagani et al 2016, 100 Mitochondrial genomes from Modern Egyptians
        assertEquals(output.size(), 100);
        assertEquals(output.containsKey(">egypt.14AJ129"), true);



    }

    @Test
    public void io_test_genericInput(){
       // setUp("/generic_test_input.tsv");


    }

    @Test
    public void io_test_hsd(){
       // setUp("/test_input_hsd.tsv");

    }





}
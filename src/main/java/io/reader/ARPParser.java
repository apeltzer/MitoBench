package io.reader;

import database.ColumnNameMapper;
import io.Exceptions.ARPException;
import io.IInputData;
import io.datastructure.Entry;
import io.datastructure.generic.GenericInputData;
import io.inputtypes.CategoricInputType;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by peltzer on 24/11/2016.
 */
public class ARPParser implements IInputData {
    private HashMap<String, List<Entry>> map = new HashMap<>();
    private Logger LOG;
    private Set<String> message_duplicates;

    public ARPParser(String file, Logger logger, Set<String> message_duplications) throws IOException, ARPException {
        LOG = logger;
        LOG.info("Read ARP file: " + file);
        FileReader fr = new FileReader(file);
        BufferedReader bfr = new BufferedReader(fr);
        ColumnNameMapper mapper = new ColumnNameMapper();
        this.message_duplicates = message_duplications;

        String currline;
        boolean init = true;
        String currGroup = "";
        while ((currline = bfr.readLine()) != null) {
            currline = currline.trim();
            if(init){
                init = false;
                //check if format is indeed ARP format
                if(currline.startsWith("[Profile]")){
                    continue;
                } else {
                    throw new ARPException("This is not in ARP Format!");
                }

            } else {
                if(currline.contains("SampleName")){
                    //then we are inside the view.data block
                    String[] split = currline.split("\"");
                    currGroup = split[1];
                    continue;
                }
                if(currline.contains("[[Structure]]")){
                    break; //Then we are done with actual view.data, grouping is parsed differently by us...
                }
                if(currline.isEmpty() | currline.startsWith("}") |  currline.startsWith("SampleSize") |
                        currline.startsWith("SampleData") | currline.startsWith("Title") | currline.startsWith("NbSamples") |
                        currline.startsWith("GameticPhase") |  currline.startsWith("RecessiveData") |
                        currline.startsWith("DataType") | currline.startsWith("LocusSeparator") | currline.startsWith("MissingData") |
                        currline.startsWith("GenotypicData") | currline.startsWith("[Data]") | currline.startsWith("[[Samples]]")) {
                    continue;
                } else {
                    String[] dataSplit = currline.split("\t");
                    int array_index = 0;
                    while(dataSplit[array_index].length() == 0)
                        array_index++;
                    String id = dataSplit[array_index];
                    if(id.matches(".*[^\\d]\\d{1}$")){
                        id = id.split("\\.")[0];
                    }
                    String mtseq = dataSplit[array_index+2];
                    List<Entry> entries = new ArrayList<>();
                    Entry e = new Entry(mapper.mapString("mt_sequence"), new CategoricInputType("String"), new GenericInputData(mtseq));
                    Entry e_group = new Entry(mapper.mapString("ARP-Groups"), new CategoricInputType("String"), new GenericInputData(currGroup));
                    entries.add(e);
                    entries.add(e_group);


                    // Duplicates within input file are not allowed!
                    if(map.keySet().contains(id)){
                        message_duplicates.add(id);
//                        DuplicatesException duplicatesException = new DuplicatesException("The input file contains duplicates: " + id +
//                                "\nOnly first hit will be added");
//                        DuplicatesErrorDialogue duplicatesErrorDialogue = new DuplicatesErrorDialogue(duplicatesException);
                    } else {
                        map.put(id , entries);
                    }


                }
            }
        }
    }



    @Override
    public HashMap<String, List<Entry>> getCorrespondingData() {

        return this.map;
    }
}

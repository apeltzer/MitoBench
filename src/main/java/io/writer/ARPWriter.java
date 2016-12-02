package io.writer;

import io.IOutputData;
import io.datastructure.arp.ArpProfile;
import io.datastructure.arp.ArpSample;
import io.datastructure.arp.ArpStructure;
import io.datastructure.fastA.FastaEntry;
import javafx.scene.control.TableColumn;
import view.table.TableController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by peltzer on 30/11/2016.
 */
public class ARPWriter implements IOutputData {
    private FileWriter fileWriter;
    private BufferedWriter bfWriter;
    private String groups = "";
    private int groupSize = 0;
    private HashMap<String, String> regions;
    private TableController tableController;


    public ARPWriter(TableController tableController) {
        this.tableController = tableController;
    }

    @Override
    public void writeData(String file) throws IOException {
        //Initialize properly
        if (!file.endsWith("arp")) {
            file = file + ".arp";
        }

        fileWriter = new FileWriter(new File(file));
        bfWriter = new BufferedWriter(fileWriter);

        getRegions();
        //Write profile first

        ArpProfile arpprofile = new ArpProfile(file, String.valueOf(groupSize), "DNA", "N");
        bfWriter.write(arpprofile.getProfile());
        ArpSample arpsample = new ArpSample(getSequenceData(), regions, groupSize);
        bfWriter.write(arpsample.getARPSample());
        ArpStructure arpstructure = new ArpStructure(groupSize, getGroupCounts(regions));
        bfWriter.write(arpstructure.getArpStructure());
        bfWriter.flush();
        bfWriter.close();


    }

    @Override
    public void setGroups(String groupID) {
        this.groups = groupID;
        getNumberofGroups();
    }

    private void getNumberofGroups() {
        Set<String> setme = new HashSet<>();
        TableColumn tbclm = tableController.getTableColumnByName(groups);
        tableController.getTable().getItems().stream().forEach((o)
                -> setme.add((String) tbclm.getCellData(o)));
        this.groupSize = setme.size();
    }


    private ArrayList<FastaEntry> getSequenceData() {
        ArrayList<FastaEntry> list = new ArrayList<>();
        TableColumn tbclm_seq = tableController.getTableColumnByName("MTSequence");
        TableColumn tbclm_id = tableController.getTableColumnByName("ID");
        // write view.data

        //TODO this currently exports not the entire FastA sequence properly, @Judith - update this once you have figured out where this is :_)
        tableController.getTable().getItems().stream().forEach((o)
                -> list.add(new FastaEntry((String) tbclm_seq.getCellData(o), (String) tbclm_id.getCellData(o)))
        );

        return list;
    }

    private void getRegions() {
        HashMap hmp = new HashMap();
        TableColumn tbclm_region = tableController.getTableColumnByName(groups);
        TableColumn tbclm_id = tableController.getTableColumnByName("ID");

        tableController.getTable().getItems().stream().forEach(o
                -> hmp.put((String) tbclm_id.getCellData(o), (String) tbclm_region.getCellData(o)));

        this.regions = hmp;
    }


    private HashMap<String, Integer> getGroupCounts(HashMap<String, String> hmpInput) {
        HashMap hmp = new HashMap();
        for (String key : hmpInput.keySet()) {
            String value = hmpInput.get(key);
            if (hmp.containsKey(value)) {
                hmp.put(value, (Integer) hmp.get(value) + 1);
            } else {
                hmp.put(value, 1);
            }
        }
        return hmp;
    }

}

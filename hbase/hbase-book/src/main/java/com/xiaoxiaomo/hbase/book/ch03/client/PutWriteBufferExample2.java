package com.xiaoxiaomo.hbase.book.ch03.client;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import util.HBaseHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *  using the client-side write buffer List
 *
 */
public class PutWriteBufferExample2 {

    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();

        HBaseHelper helper = HBaseHelper.getHelper(conf);
        helper.dropTable("testtable");
        helper.createTable("testtable", "colfam1");

        Connection connection = ConnectionFactory.createConnection(conf);
        BufferedMutator mutator = connection.getBufferedMutator(TableName.valueOf("testtable"));

        // Create a list to hold all mutations.
        List<Mutation> mutations = new ArrayList<Mutation>();

        Put put1 = new Put(Bytes.toBytes("row1"));
        put1.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"), Bytes.toBytes("val1"));
        mutations.add(put1); //Add Put instance to list of mutations.

        Put put2 = new Put(Bytes.toBytes("row2"));
        put2.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"), Bytes.toBytes("val2"));
        mutations.add(put2);

        Put put3 = new Put(Bytes.toBytes("row3"));
        put3.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"), Bytes.toBytes("val3"));
        mutations.add(put3);

        mutator.mutate(mutations); // Store some rows with columns into HBase.

        // get
        Table table = connection.getTable(TableName.valueOf("testtable"));
        Get get = new Get(Bytes.toBytes("row1"));
        Result res1 = table.get(get);
        System.out.println("Result: " + res1); // Try to load previously stored row, this will print "Result: keyvalues=NONE".

        mutator.flush(); // Force a flush, this causes an RPC to occur.

        Result res2 = table.get(get);
        System.out.println("Result: " + res2); //  Now the row is persisted and can be loaded.
        // ^^ PutWriteBufferExample2
        mutator.close();
        table.close();
        connection.close();
        helper.close();
    }
}

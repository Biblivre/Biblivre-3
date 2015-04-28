/*
 * Title:       JDBCResultSet
 * @version:    $Id: JDBCResultSet.java,v 1.15 2005/10/20 15:39:53 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description:
 *
 */

/*
 * $Log: JDBCResultSet.java,v $
 * Revision 1.15  2005/10/20 15:39:53  ibbo
 * Temp files locations can now be influenced with the JVM flag com.k_int.inode.tmpdir
 *
 * Revision 1.14  2005/10/20 14:18:03  ibbo
 * updated
 *
 * Revision 1.13  2005/10/18 12:13:18  ibbo
 * Updated
 *
 * Revision 1.12  2005/02/18 09:24:22  ibbo
 * Added getResultSet info to all RS impls
 *
 * Revision 1.11  2004/10/31 15:52:46  ibbo
 * Updated
 *
 * Revision 1.10  2004/10/31 12:21:22  ibbo
 * Database criteria added
 *
 * Revision 1.9  2004/10/29 10:11:23  ibbo
 * Minor revision to aggregating result set close functions
 *
 * Revision 1.8  2004/10/28 15:13:46  ibbo
 * JDBC Query can now map a use attribute onto multiple database columns which
 * will be OR'd together
 *
 * Revision 1.7  2004/10/28 12:31:41  ibbo
 * Moved to new framework for constructing searchable which passes in ApplicationContext
 *
 * Revision 1.6  2004/10/27 14:41:20  ibbo
 * XML record export working
 *
 * Revision 1.5  2004/10/26 16:42:23  ibbo
 *
 * Updated
 *
 * Revision 1.4  2004/10/26 15:30:52  ibbo
 * Updated
 *
 * Revision 1.3  2004/10/26 11:28:38  ibbo
 * Updated
 *
 * Revision 1.2  2004/10/24 15:33:58  ibbo
 * Updated
 *
 * Revision 1.1  2004/10/24 15:18:31  ibbo
 * updated
 *
 */
package biblivre3.z3950;

import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;

import com.k_int.sql.data_dictionary.*;

import jdbm.*;
import jdbm.helper.LongComparator;
import jdbm.btree.BTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jzkit.search.provider.jdbc.JDBCSearchable;

/**
 * @author Ian Ibbotson
 * @version $Id: JDBCResultSet.java,v 1.15 2005/10/20 15:39:53 ibbo Exp $
 */
public class JDBCResultSet extends AbstractIRResultSet implements IRResultSet {

    private static Log log = LogFactory.getLog(AbstractIRResultSet.class);
    int num_hits = 0;
    private jdbm.RecordManager recman = null;
    private BTree tree = null;

    // private String base_entity_name = null;
    private String results_file_name;
    private static long instance_counter = 0;

    private JDBCResultSet() {
        log.info("JDBCResultSet() " + (++instance_counter));
    }

    public JDBCResultSet(JDBCSearchable owner) {
        super();
        log.info("New JDBCResultSet:" + (++instance_counter));
    }

    @Override
    protected void finalize() {
        log.info("JDBCResultSet::finalize" + (--instance_counter));
    }

    public void init() {
        // Set up temp results file
        try {
            java.io.File results_file = null;
            String dir = System.getProperty("com.k_int.inode.tmpdir");
            if (dir != null) {
                results_file = java.io.File.createTempFile("JDBCRS", "jdbm", new java.io.File(dir + "/jdbc"));
            } else {
                results_file = java.io.File.createTempFile("JDBCRS", "jdbm");
            }

            results_file_name = results_file.toString();
            java.util.Properties props = new java.util.Properties();
            props.put(RecordManagerOptions.CACHE_SIZE, "500");
            props.put(RecordManagerOptions.DISABLE_TRANSACTIONS, "true");
            recman = RecordManagerFactory.createRecordManager(results_file_name, props);
            results_file.delete();
            tree = BTree.createInstance(recman, new LongComparator());
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // Fragment Source methods
    @Override
    public InformationFragment[] getFragment(int starting_fragment, int count, RecordFormatSpecification spec) throws IRResultSetException {
        InformationFragment[] result = new InformationFragment[count];
        try {
            for (int i = 0; i < count; i++) {
                long recno = starting_fragment - 1 + i;
                OID oid = (OID) tree.find(new Long(recno));
                String record = (String)oid.getKeyPairs().getAttrValue("record");
                result[i] = new iso2709(record.getBytes(), "UTF-8");
                result[i].setHitNo(recno+1);
            }
        } catch (java.io.IOException ioe) {
            throw new IRResultSetException("Problem retrieving record", ioe);
        } 
        return result;
    }

    @Override
    public void asyncGetFragment(int starting_fragment, int count, RecordFormatSpecification spec, IFSNotificationTarget target) {
        try {
            InformationFragment[] result = getFragment(starting_fragment, count, spec);
            target.notifyRecords(result);
        } catch (IRResultSetException re) {
            target.notifyError("JDBC", new Integer(0), "No reason", re);
        }
    }

    /** Current number of fragments available */
    @Override
    public int getFragmentCount() {
        return num_hits;
    }

    /** The size of the result set (Estimated or known) */
    @Override
    public int getRecordAvailableHWM() {
        return num_hits;
    }

    /** Release all resources and shut down the object */
    @Override
    public void close() {
        log.info("JDBCResultSet::close() ");
        try {
            recman.close();
            log.info("Deleting JDBC Results " + results_file_name + "[.db,.lg]");
            java.io.File f = new java.io.File(results_file_name + ".db");
            f.delete();
            f = null;
            f = new java.io.File(results_file_name + ".lg");
            f.delete();
            f = null;
        } catch (java.io.IOException ioe) {
            log.warn("Problem deleting temp files", ioe);
            ioe.printStackTrace();
        }
    }

    public void add(OID key) {
        try {
            tree.insert(new Long(num_hits++), key, false);
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
    }

    protected void commit() {
        try {
            recman.commit();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    public IRResultSetInfo getResultSetInfo() {
        return new IRResultSetInfo(getResultSetName(), "JDBC", null, getFragmentCount(), getStatus(), null);
    }
}

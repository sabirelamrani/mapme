package com.db4o.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.db4o.android.MapBookmark;
import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.Query;

//import java.io.File;

/**
 * @author German Viscuso
 *
 */
public class Db4oHelper {
	
	private static ObjectContainer oc = null;
	private Context context;
	
	/**
     * 
     * @param ctx
     */
    public Db4oHelper(Context ctx) {
    	context = ctx;
    }
    
    public ObjectContainer db(){
    	try {
    		if(oc == null || oc.ext().isClosed()){
    			//new File(db4oDBFullPath(context)).delete();
    			oc = Db4o.openFile(dbConfig(), db4oDBFullPath(context));
    		}
    		return oc;
    	} catch (Exception e) {
        	Log.e(Db4oHelper.class.getName(), e.toString());
        	return null;
        }
    }
    
    private Configuration dbConfig(){
    	Configuration c = Db4o.newConfiguration();
    	c.objectClass(MapBookmark.class).objectField("name").indexed(true); 
    	c.objectClass(MapBookmark.class).updateDepth(3);
    	c.objectClass(MapBookmark.class).minimumActivationDepth(3);
    	c.objectClass(MapBookmark.class).cascadeOnDelete(true);
    	return c;
    }
	
	private String db4oDBFullPath(Context ctx) {
		return ctx.getDataDir() + "/" + "browsemap.db4o";
	}
	
	/**
     * Close database connection
     */
    public void close() {
    	if(oc != null)
    		oc.close();
    }
    
    public void setBookmark(
    				String name, 
    				String description, 
    				int latitude,
    				int longitude,
    				int zoomLevel,
    				boolean satellite,
    				boolean traffic){
    	
    	MapBookmark bkm = getBookmark(name);
    	if(bkm == null)
    		bkm = new MapBookmark(name);
    	bkm.setDescription(description);
		bkm.setLatitude(latitude);
		bkm.setLongitude(longitude);
		bkm.setZoomLevel(zoomLevel);
		bkm.setSatellite(satellite);
		bkm.setTraffic(traffic);
    	db().set(bkm);
    	db().commit();
    }
    
    public MapBookmark getBookmark(String name){
    	MapBookmark proto = new MapBookmark(name);
    	ObjectSet result = db().get(proto);
    	if(result.hasNext()){
    		return (MapBookmark)result.next();
    	}
    	return null;
    }
    
    public List<MapBookmark> getBookmarkList(){
    	ArrayList<MapBookmark> ret = new ArrayList<MapBookmark>();
        ObjectSet result = getBookmarks();
        while (result.hasNext())
        	ret.add((MapBookmark)result.next());
        return ret;
    }
    
    private ObjectSet getBookmarks(){
    	Query query = db().query();
    	query.constrain(MapBookmark.class);
    	query.descend("name").orderAscending();
    	return query.execute();
    }

    public void deleteBookmark(String name) {
    	MapBookmark bkm = getBookmark(name);
    	if(bkm != null){
    		db().delete(bkm);
    		db().commit();
    	}
    }
    
    public int bookamrkCount(){
    	return getBookmarks().size();
    }
    
}


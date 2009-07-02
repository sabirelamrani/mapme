package com.db4o.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.db4o.android.MapBookmark;
import com.db4o.*;
import com.db4o.config.*;
//import com.db4o.internal.ObjectContainerBase;
//import com.db4o.internal.query.Db4oQueryExecutionListener;
//import com.db4o.internal.query.NQOptimizationInfo;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.google.android.maps.GeoPoint;


/**
 * @author German Viscuso
 *
 */
public class Db4oHelper {
	
	private static ObjectContainer oc = null;
	private Context context;
	private final String dbName = "bookmarks.db4o";
	
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
    			/*((ObjectContainerBase)oc).getNativeQueryHandler().addListener(new Db4oQueryExecutionListener() {
					public void notifyQueryExecuted(NQOptimizationInfo info) {
						System.err.println(info);
					}
    			});*/
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
    	c.objectClass(MapBookmark.class).updateDepth(6);
    	c.objectClass(MapBookmark.class).minimumActivationDepth(6);
    	c.objectClass(MapBookmark.class).cascadeOnDelete(true);
    	return c;
    }
	
	private String db4oDBFullPath(Context ctx) {
		return ctx.getDir("data", 0) + "/" + dbName;
	}
	
	/**
     * Close database connection
     */
	public void close() {
    	if(oc != null)
    		oc.close();
    }
	
	public void setBookmark(MapBookmark bookmark){
		setBookmark(
				bookmark.name, 
				bookmark.description, 
				bookmark.latitude,
				bookmark.longitude,
				bookmark.zoomLevel,
				bookmark.satellite,
				bookmark.traffic);
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
    	db().store(bkm);
    	db().commit();
    }
    
	public MapBookmark getBookmark(String name){
    	MapBookmark proto = new MapBookmark(name);
    	ObjectSet<MapBookmark> result = db().queryByExample(proto);
    	if(result.hasNext())
    		return (MapBookmark)result.next();
    	return null;
    }
    
    @SuppressWarnings("unchecked")
	public List<MapBookmark> getBookmarkList(){
    	ArrayList<MapBookmark> ret = new ArrayList<MapBookmark>();
        ObjectSet result = getBookmarks();
        while (result.hasNext())
        	ret.add((MapBookmark)result.next());
        return ret;
    }
    
    @SuppressWarnings("unchecked")
	public ObjectSet getBookmarks(){
    	Query query = db().query();
    	query.constrain(MapBookmark.class);
    	query.descend("name").orderAscending();
    	return query.execute();
    }
    
    @SuppressWarnings("serial")
	public List<MapBookmark> getNearbyBookmarks(
    		final GeoPoint mapCenter, final int latitudeSpan, final int longitudeSpan){
    	return db().query(new Predicate<MapBookmark>() {
            public boolean match(MapBookmark candidate) {
            	boolean inLatitude = 
            		(candidate.latitude <= (mapCenter.getLatitudeE6() + latitudeSpan/2)) &&
            		(candidate.latitude >= (mapCenter.getLatitudeE6() - latitudeSpan/2));
            	boolean inLongitude = 
            		(candidate.longitude <= (mapCenter.getLongitudeE6() + longitudeSpan/2)) &&
            		(candidate.longitude >= (mapCenter.getLongitudeE6() - longitudeSpan/2));
                return inLatitude && inLongitude;
            }
        });
    }
    
    @SuppressWarnings("serial")
	public List<MapBookmark> getNearbyBookmarks(
    		final GeoPoint mapCenter, final int tolerance){
    	return db().query(new Predicate<MapBookmark>() {
            public boolean match(MapBookmark candidate) {
            	boolean inLatitude = 
            		(candidate.latitude <= (mapCenter.getLatitudeE6() + tolerance)) &&
            		(candidate.latitude >= (mapCenter.getLatitudeE6() - tolerance));
            	boolean inLongitude = 
            		(candidate.longitude <= (mapCenter.getLongitudeE6() + tolerance)) &&
            		(candidate.longitude >= (mapCenter.getLongitudeE6() - tolerance));
                return inLatitude && inLongitude;
            }
        });
    }
    
    @SuppressWarnings("serial")
	public List<MapBookmark> getBookmarksByKeyword(final String keyword){
    	return db().query(new Predicate<MapBookmark>() {
            public boolean match(MapBookmark candidate) {
            	return 
            		candidate.getName().toLowerCase().contains(keyword) || 
            		candidate.getDescription().toLowerCase().contains(keyword);
            }
        });
    }

    public void deleteBookmark(String name) {
        //Search by name
    	MapBookmark bkm = getBookmark(name);
        //Delete object
    	if(bkm != null){
    		db().delete(bkm);
    		db().commit();
    	}
    }
    
    public int bookmarkCount(){
    	return getBookmarks().size();
    }
    
}


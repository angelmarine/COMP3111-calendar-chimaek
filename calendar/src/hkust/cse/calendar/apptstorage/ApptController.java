package hkust.cse.calendar.apptstorage;

import hkust.cse.calendar.notification.NotificationController;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Notification;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ApptController {
	
	/* Applying Singleton Structure */
	private static ApptController instance = null;
	private static int apptIDCount = 1;
	public static final int DAILY = 1, WEEKLY = 2, MONTHLY = 3;
	@Deprecated
	public final static int REMOVE = 1;
	@Deprecated	
	public final static int MODIFY = 2;
	@Deprecated	
	public final static int NEW = 3;

	/* The Appt storage */
	private static ApptStorage mApptStorage = null;

	/* Empty Constructor, since in singleton getInstance() is used instead*/
	public ApptController() {
		
	}
	
	public static ApptController getInstance(){
		if (instance == null){
			instance = new ApptController();
		}
		return instance;
	}
	
	//Initialize mApptStorage. Returns false if ApptStorage object already exists
	public boolean initApptStorage(ApptStorage storage){
		if (mApptStorage == null){
			mApptStorage = storage;
			return true;
		}
		return false;
	}
	

	/* Retrieve the Appt's in the storage for a specific user within the specific time span */
	@Deprecated
	public Appt[] RetrieveAppts(User entity, TimeSpan time) {
		return mApptStorage.RetrieveAppts(entity, time);
	}
	
	public List<Appt> RetrieveApptsInList(User user, TimeSpan time){
		return mApptStorage.RetrieveApptsInList(time);
	}

	// overload method to retrieve appointment with the given joint appointment id
	public Appt RetrieveAppts(int joinApptID) {
		return mApptStorage.RetrieveAppts(joinApptID);
	}
	
	/* Manage the Appt in the storage
	 * parameters: the Appt involved, the action to take on the Appt */
	@Deprecated
	public void ManageAppt(Appt appt, int action) {

		if (action == NEW) {				// Save the Appt into the storage if it is new and non-null
			if (appt == null)
				return;
			mApptStorage.SaveAppt(appt);
		} else if (action == MODIFY) {		// Update the Appt in the storage if it is modified and non-null
			if (appt == null)
				return;
			mApptStorage.UpdateAppt(appt);
		} else if (action == REMOVE) {		// Remove the Appt from the storage if it should be removed
			mApptStorage.RemoveAppt(appt);
		} 
	}
	
	//Register appt as New Appt of user. Return true if successfully registered
	public boolean saveNewAppt(User user, Appt appt){
		appt.setID(apptIDCount++);
		return mApptStorage.SaveAppt(appt);
	}
	
	public boolean saveRepeatedNewAppt(User user, Appt appt, int repeatType, Date repeatEndDate){
		List<Appt> tmpList;
		tmpList = getRepeatedApptList(appt, repeatType, repeatEndDate);
		if (mApptStorage.checkOverlaps(tmpList))
			return false;
		for (Appt a : tmpList){
			if (!saveNewAppt(user, a))
				return false;
		}
		return true;
	}
	
	
	public List<Appt> getRepeatedApptList(Appt appt, int repeatType, Date repeatEndDate){
		ArrayList<Appt> list = new ArrayList<Appt>();
		Calendar startTime = Calendar.getInstance();
		Calendar endTime = Calendar.getInstance();
		startTime.setTime(new Date(appt.getTimeSpan().StartTime().getTime()));
		endTime.setTime(new Date(appt.getTimeSpan().EndTime().getTime()));
		
		Appt i = new Appt(appt);
		i.setNextRepeatedAppt(null);
		i.setPreviousRepeatedAppt(null);
		list.add(i);
		
		Appt j = new Appt(appt);
		while(true){
			if (repeatType == DAILY){
				startTime.add(Calendar.DATE, 1);
				endTime.add(Calendar.DATE, 1);
			}
			else if (repeatType == WEEKLY){
				startTime.add(Calendar.DATE, 7);
				endTime.add(Calendar.DATE, 7);
			}
			else if (repeatType == MONTHLY){
				startTime.add(Calendar.MONTH, 1);
				endTime.add(Calendar.MONTH, 1);
			}
			if (endTime.getTime().after(repeatEndDate))
				break;
			j.setTimeSpan(new TimeSpan(startTime.getTimeInMillis(), endTime.getTimeInMillis()));
			j.setNextRepeatedAppt(null);
			i.setNextRepeatedAppt(j);
			j.setPreviousRepeatedAppt(i);
			list.add(j);
			i = j;
			j = new Appt(i);
		}
		return list;
	}
	
	//Modify appt of user. Return true if successfully modified
	public boolean modifyAppt(User user, Appt appt){
		return mApptStorage.UpdateAppt(appt);
	}
	
	//Remove appt of user. Return true if successfully removed
	public boolean removeAppt(User user, Appt appt){
		//If repeated, then remove all repeated appts. However, past appts will not be removed
		if (appt.isRepeated()){
			System.out.println("\nRemove Repeated!");
			Appt iterator = appt.getNextRepeatedAppt();
			while (iterator!=null){
				mApptStorage.RemoveAppt(iterator);
				iterator = iterator.getNextRepeatedAppt();
			}
			iterator = appt.getPreviousRepeatedAppt();
			while (iterator!=null){
				mApptStorage.RemoveAppt(iterator);
				iterator = iterator.getPreviousRepeatedAppt();
			}
			mApptStorage.RemoveAppt(appt);
			return true;
		}
		return mApptStorage.RemoveAppt(appt);
	}
	
	public boolean setNotificationForAppt(Appt appt, 
			boolean flagOne, boolean flagTwo, boolean flagThree, boolean flagFour){
		
		Notification noti = new Notification(appt.getTitle(), appt.getTimeSpan().StartTime(),
				flagOne, flagTwo, flagThree, flagFour);
		appt.setNotification(noti);
		return NotificationController.getInstance().saveNewNotification(noti);
	}
	
	/* Get the defaultUser of mApptStorage */
	public User getDefaultUser() {
		return mApptStorage.getDefaultUser();
	}

	// method used to load appointment from xml record into hash map
	public void LoadApptFromXml(){
		mApptStorage.LoadApptFromXml();
	}
}

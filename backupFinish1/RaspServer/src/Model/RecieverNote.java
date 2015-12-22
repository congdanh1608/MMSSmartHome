package Model;

public class RecieverNote {
	private String noteJson;
	private String Reciever;
	
	public RecieverNote(){
		noteJson = null;
		Reciever = null;
	}
	
	public RecieverNote(String note, String reciever){
		this.noteJson = note;
		this.Reciever = reciever;
	}
	
	public String getReciever() {
		return Reciever;
	}
	
	public void setReciever(String reciever) {
		Reciever = reciever;
	}

	public String getNoteJson() {
		return noteJson;
	}
	
	public void setNoteJson(String noteJson) {
		this.noteJson = noteJson;
	}
}

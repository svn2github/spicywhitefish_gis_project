
public class ApplicationEvent {
	private String type;
	private Object payload;
	private boolean consumed;
	
	public ApplicationEvent(String type, Object payload) {
		this.consumed=false;
		this.type=type;
		this.payload=payload;
	}
	
	public String getType() {
		return this.type;
	}
	public Object getPayload() {
		return this.payload;
	}
	public void consume() {
		this.consumed=true;
	}
}

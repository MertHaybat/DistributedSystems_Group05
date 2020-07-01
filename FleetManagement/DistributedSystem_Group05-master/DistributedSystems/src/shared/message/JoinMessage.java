package shared.message;

import java.io.Serializable;
import java.util.UUID;

import server.cluster.Member;

public class JoinMessage implements Serializable{

		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private final Member member;

	    public JoinMessage(Member member) {
	     
	        this.member = member;
	    }

		public Member getMember() {
			return member;
		}

	    

	   
}

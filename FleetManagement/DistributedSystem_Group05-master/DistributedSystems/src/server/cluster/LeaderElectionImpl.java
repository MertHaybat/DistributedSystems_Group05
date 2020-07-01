package server.cluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderElectionImpl {

	private final Map<Integer, Member> members;
	public LeaderElectionImpl(Map<Integer, Member> members) {
		this.members = members;
	}


	public Map<Integer, Member> electionResult() {

		List<Member> memberList = new ArrayList<Member>();
		for (Map.Entry<Integer, Member> entry : members.entrySet()) {
			memberList.add(entry.getValue());
		}

		Collections.sort(memberList, new SortMembers());

		int highestId = 0;

		if (memberList.size() == 1) {
			for (Member member : memberList) {
				member.setLeader(true);
			}
		} else {

			for (Member member : memberList) {
				for (Member member2 : memberList) {
					if (member.getMemberId() == member2.getMemberId()) {
						break;
					} else if (member.getMemberId() < member2.getMemberId()) {
						break;
					} else if (member.getMemberId() > member2.getMemberId()) {
						highestId = member.getMemberId();
					}
				}
			}
			for (Member member2 : memberList) {
				if (member2.getMemberId() == highestId) {
					member2.setLeader(true);
				} else {
					member2.setLeader(false);
				}
			}
		}

		Map<Integer, Member> membersAfterElection = new HashMap<Integer, Member>();
		for (Member member : memberList) {
			membersAfterElection.put(member.getMemberId(), member);
		}
		
		return membersAfterElection;

	}

	private class SortMembers implements Comparator<Member> {

		@Override
		public int compare(Member o1, Member o2) {
			return o1.getMemberId() - o2.getMemberId();
		}

	}

}

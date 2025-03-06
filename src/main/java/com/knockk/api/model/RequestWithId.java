package com.knockk.api.model;

public class RequestWithId {
		private String residentId;
		// private Boolean withCredentials;

		public void setResidentId(String residentId) {
            this.residentId = residentId;
        }

        // public void setWithCredentials(Boolean withCredentials) {
        //     this.withCredentials = withCredentials;
        // }

        public String getResidentId (){
			return residentId;
		}

		// public boolean isWithCredentials (){
		// 	return withCredentials;
		// }

}

package com.leeAutumn;

import com.leeAutumn.TokenAnalyser.idenType;

public class Variable {
		
	
        public void setName(String name) {
            this.name = name;
        }

        public void setType(idenType type) {
            this.type = type;
        }

        public void setValue(double value) {
            this.value = value;
        }

        String name="";
        idenType type=idenType.INT;

        public void setSet(boolean set) {
            isSet = set;
        }

        boolean isSet=false;
        
        double value=0;

        public String getName() {
			return name;
		}

		public idenType getType() {
			return type;
		}

		public double getValue() {
			return value;
		}

		public Variable(String name){
            this.name=name;
        }
}

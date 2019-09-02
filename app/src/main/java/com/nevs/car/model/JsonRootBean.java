package com.nevs.car.model;

import java.util.List;

/**
 * Created by mac on 2018/8/6.
 */
    public class JsonRootBean {

        private List<Items> items;
        private String resultMessage;
        private String resultDescription;
    public class Items {

        private String equipmentID;
        private String manufacturerName;
        private String equipmentType;
        private int power;
        private List<Connectors> connectors;
        public void setEquipmentID(String equipmentID) {
            this.equipmentID = equipmentID;
        }
        public String getEquipmentID() {
            return equipmentID;
        }

        public void setManufacturerName(String manufacturerName) {
            this.manufacturerName = manufacturerName;
        }
        public String getManufacturerName() {
            return manufacturerName;
        }

        public void setEquipmentType(String equipmentType) {
            this.equipmentType = equipmentType;
        }
        public String getEquipmentType() {
            return equipmentType;
        }

        public void setPower(int power) {
            this.power = power;
        }
        public int getPower() {
            return power;
        }

        public void setConnectors(List<Connectors> connectors) {
            this.connectors = connectors;
        }
        public List<Connectors> getConnectors() {
            return connectors;
        }


        public class Connectors {

            private String connectorID;
            private String connectorName;
            private int connectorType;
            private int current;
            private int voltageUpperLimits;
            private int voltageLowerLimits;
            private int power;
            private String parkNo;
            private int chargingStatus;
            private int parkStatus;
            private int lockStatus;
            private int nationalStandard;
            private long statusUpdateTinme;
            public void setConnectorID(String connectorID) {
                this.connectorID = connectorID;
            }
            public String getConnectorID() {
                return connectorID;
            }

            public void setConnectorName(String connectorName) {
                this.connectorName = connectorName;
            }
            public String getConnectorName() {
                return connectorName;
            }

            public void setConnectorType(int connectorType) {
                this.connectorType = connectorType;
            }
            public int getConnectorType() {
                return connectorType;
            }

            public void setCurrent(int current) {
                this.current = current;
            }
            public int getCurrent() {
                return current;
            }

            public void setVoltageUpperLimits(int voltageUpperLimits) {
                this.voltageUpperLimits = voltageUpperLimits;
            }
            public int getVoltageUpperLimits() {
                return voltageUpperLimits;
            }

            public void setVoltageLowerLimits(int voltageLowerLimits) {
                this.voltageLowerLimits = voltageLowerLimits;
            }
            public int getVoltageLowerLimits() {
                return voltageLowerLimits;
            }

            public void setPower(int power) {
                this.power = power;
            }
            public int getPower() {
                return power;
            }

            public void setParkNo(String parkNo) {
                this.parkNo = parkNo;
            }
            public String getParkNo() {
                return parkNo;
            }

            public void setChargingStatus(int chargingStatus) {
                this.chargingStatus = chargingStatus;
            }
            public int getChargingStatus() {
                return chargingStatus;
            }

            public void setParkStatus(int parkStatus) {
                this.parkStatus = parkStatus;
            }
            public int getParkStatus() {
                return parkStatus;
            }

            public void setLockStatus(int lockStatus) {
                this.lockStatus = lockStatus;
            }
            public int getLockStatus() {
                return lockStatus;
            }

            public void setNationalStandard(int nationalStandard) {
                this.nationalStandard = nationalStandard;
            }
            public int getNationalStandard() {
                return nationalStandard;
            }

            public void setStatusUpdateTinme(long statusUpdateTinme) {
                this.statusUpdateTinme = statusUpdateTinme;
            }
            public long getStatusUpdateTinme() {
                return statusUpdateTinme;
            }

        }


    }
        public void setItems(List<Items> items) {
            this.items = items;
        }
        public List<Items> getItems() {
            return items;
        }

        public void setResultMessage(String resultMessage) {
            this.resultMessage = resultMessage;
        }
        public String getResultMessage() {
            return resultMessage;
        }

        public void setResultDescription(String resultDescription) {
            this.resultDescription = resultDescription;
        }
        public String getResultDescription() {
            return resultDescription;
        }
}

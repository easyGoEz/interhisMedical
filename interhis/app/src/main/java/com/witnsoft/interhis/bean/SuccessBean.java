package com.witnsoft.interhis.bean;

/**
 * Created by ${liyan} on 2017/6/28.
 */

public class SuccessBean {


    /**
     * VERSION : 1.0
     * TN : F27.APP.01.06
     * RESULT : 200
     * RESULTTEXT : 处理成功
     * REPARAM :
     * DATAOBJ : {"DTYPE":"F27.APP.01.06.A","DNO":"0","DATA":{"acid":"14782"}}
     */

    private String VERSION;
    private String TN;
    private String RESULT;
    private String RESULTTEXT;
    private String REPARAM;
    private DATAOBJBean DATAOBJ;

    public String getVERSION() {
        return VERSION;
    }

    public void setVERSION(String VERSION) {
        this.VERSION = VERSION;
    }

    public String getTN() {
        return TN;
    }

    public void setTN(String TN) {
        this.TN = TN;
    }

    public String getRESULT() {
        return RESULT;
    }

    public void setRESULT(String RESULT) {
        this.RESULT = RESULT;
    }

    public String getRESULTTEXT() {
        return RESULTTEXT;
    }

    public void setRESULTTEXT(String RESULTTEXT) {
        this.RESULTTEXT = RESULTTEXT;
    }

    public String getREPARAM() {
        return REPARAM;
    }

    public void setREPARAM(String REPARAM) {
        this.REPARAM = REPARAM;
    }

    public DATAOBJBean getDATAOBJ() {
        return DATAOBJ;
    }

    public void setDATAOBJ(DATAOBJBean DATAOBJ) {
        this.DATAOBJ = DATAOBJ;
    }

    public static class DATAOBJBean {
        /**
         * DTYPE : F27.APP.01.06.A
         * DNO : 0
         * DATA : {"acid":"14782"}
         */

        private String DTYPE;
        private String DNO;
        private DATABean DATA;

        public String getDTYPE() {
            return DTYPE;
        }

        public void setDTYPE(String DTYPE) {
            this.DTYPE = DTYPE;
        }

        public String getDNO() {
            return DNO;
        }

        public void setDNO(String DNO) {
            this.DNO = DNO;
        }

        public DATABean getDATA() {
            return DATA;
        }

        public void setDATA(DATABean DATA) {
            this.DATA = DATA;
        }

        public static class DATABean {
            /**
             * acid : 14782
             */

            private String acid;

            public String getAcid() {
                return acid;
            }

            public void setAcid(String acid) {
                this.acid = acid;
            }
        }
    }
}

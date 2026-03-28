    package protoactor26;
    import unibo.basicomm23.interfaces.IApplMessage;
    import unibo.basicomm23.utils.CommUtils;

    public class Protoactor1 extends AbstractProtoactor26{
    /*
    * ------------------------------------
    * Specifica dei messaggi
    * ------------------------------------
    */
        private IApplMessage m   = CommUtils.buildDispatch(name, "do",  "2.0", "pa2");
        private IApplMessage req = CommUtils.buildRequest(name, "eval", "0.0", "pa2");
        private IApplMessage ev  = CommUtils.buildEvent(name, "info",  "pa1_working" );
        private IApplMessage autoreq = CommUtils.buildRequest(name, "eval", "4.0", "pa1");
       
        public Protoactor1(String name, ProtoActorContext26 ctx) {
            super(name, ctx);
            doJob();
        }
        protected void doJob() {
            //Invio di un dispatch al pattore pa2
            forward( m );   
            
           //Emissione di un evento
            emitInfo( ev );
    
            //Invio di una request sincrona al pattore pa2
            IApplMessage reply = request( req );   
            CommUtils.outmagenta(name + " | got reply: " + reply);

            //Invio di una request sincrona a sè stesso
            IApplMessage reply1 = request( autoreq );   
            CommUtils.outmagenta(name + " | got reply: " + reply1);
           
        }
        
    /*
    * ------------------------------------
    * Gestione dei messaggi
    * ------------------------------------
    */
        
        @Override
        protected void elabDispatch(IApplMessage m) {
            CommUtils.outblue(name + " | elabDispatch:" + m);
        }

        @Override
        protected IApplMessage elabRequest(IApplMessage req) {
            CommUtils.outblue(name + " | elabRequest:" + req);
            if( req.msgId().equals("eval")){
                double x      = Double.parseDouble(req.msgContent());
                double result = eval(x);
                IApplMessage replyMsg =
                            CommUtils.buildReply(name,req.msgId(),""+result,req.msgSender());
                return replyMsg;
            }else{
                IApplMessage replyMsg =
                CommUtils.buildReply(name,req.msgId(),
                "requestUnkown",req.msgSender());
                return replyMsg;
            }
        }

        @Override
        protected void elabReply(IApplMessage r) {
            CommUtils.outblue(name + " | elabReply:" + r);
            
        }

        @Override
        protected void elabEvent(IApplMessage ev) {
            CommUtils.outcyan(name + " | elabEvent:" + ev);
            
        }
        
    /*
    * ------------------------------------
    *  Business code
    * ------------------------------------
    */    
        protected double eval(double x) {
            CommUtils.outblue(name + " | eval: " + x);
            if (x > 4.0) {
                CommUtils.outmagenta(name + " | Simulo ritardo per x="+x);
                CommUtils.delay(10000);
            }
            return Math.sin(x) + Math.cos( Math.sqrt(3)*x);
        }

    /*
    * ------------------------------------
    * Parte proattiva
    * ------------------------------------
    */
        protected void proactiveJob(  ) {
            
        }
        
    }    


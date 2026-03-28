package protoactor26;

import unibo.basicomm23.interfaces.IApplMessage;

public interface ProtoActorContextInterface {
	public void register( AbstractProtoactor26 pactor );
	public IApplMessage elabMsg( IApplMessage am );
	public void emitInfo(IApplMessage ev);
}

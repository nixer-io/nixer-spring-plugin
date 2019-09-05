package eu.xword.nixer.nixerplugin.blocking;

import eu.xword.nixer.nixerplugin.blocking.events.BlockEvent;
import eu.xword.nixer.nixerplugin.blocking.events.BlockSourceIPEvent;
import eu.xword.nixer.nixerplugin.blocking.events.GlobalCredentialStuffingEvent;
import eu.xword.nixer.nixerplugin.blocking.events.LockUserEvent;

public interface EventVisitor {

    void accept(BlockEvent event);

    void accept(LockUserEvent event);

    void accept(BlockSourceIPEvent event);

    void accept(GlobalCredentialStuffingEvent event);
}

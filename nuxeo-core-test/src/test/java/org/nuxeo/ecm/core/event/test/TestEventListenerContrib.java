/*
 * Copyright (c) 2006-2012 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.ecm.core.event.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.event.impl.EventListenerDescriptor;
import org.nuxeo.ecm.core.event.impl.EventListenerList;
import org.nuxeo.ecm.core.event.impl.EventServiceImpl;
import org.nuxeo.ecm.core.event.script.ScriptingPostCommitEventListener;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.nuxeo.runtime.test.runner.RuntimeHarness;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@LocalDeploy("org.nuxeo.ecm.core.event:test-listeners.xml")
public class TestEventListenerContrib  {

	@Inject EventService service;
	
	@Inject RuntimeHarness harness;
	
    @Test
    public void testMerge() throws Exception {

    	EventServiceImpl serviceImpl = (EventServiceImpl) service;

    	EventListenerList listenerList = serviceImpl.getEventListenerList();
		List<EventListenerDescriptor> inLineDescs = listenerList.getInlineListenersDescriptors();
        List<EventListenerDescriptor> asyncDescs = listenerList.getAsyncPostCommitListenersDescriptors();
        List<EventListenerDescriptor> syncDescs = listenerList.getSyncPostCommitListenersDescriptors();

 
    	int inlineCount = inLineDescs.size();
    	int asyncCount = asyncDescs.size();
    	int syncCount = syncDescs.size();

        URL url = EventListenerTest.class.getClassLoader().getResource("test-listeners.xml");
        harness.deployTestContrib("org.nuxeo.ecm.core.event",url);

        assertEquals(inlineCount, inLineDescs.size());
        assertEquals(inlineCount, listenerList.getInLineListeners().size());

        // check enable flag
        EventListenerDescriptor desc =  inLineDescs.get(0);
        desc.setEnabled(false);
        serviceImpl.addEventListener(desc);
        assertEquals(inlineCount - 1, listenerList.getInLineListeners().size());

        desc.setEnabled(true);
        serviceImpl.addEventListener(desc);
        assertEquals(inlineCount, listenerList.getInLineListeners().size());

        // test PostCommit
        url = EventListenerTest.class.getClassLoader().getResource("test-PostCommitListeners.xml");
        harness.deployTestContrib("org.nuxeo.ecm.core.event", url);
        assertEquals(asyncCount+1, asyncDescs.size());
		assertEquals(asyncCount+1, listenerList.getAsyncPostCommitListeners().size());
        desc = serviceImpl.getEventListener("testPostCommit");
        assertEquals(0, desc.getPriority());

        url = EventListenerTest.class.getClassLoader().getResource("test-PostCommitListeners2.xml");
        harness.deployTestContrib("org.nuxeo.ecm.core.event", url);
        assertEquals(asyncCount, listenerList.getAsyncPostCommitListeners().size());
		assertEquals(syncCount+1, listenerList.getSyncPostCommitListeners().size());

        boolean isScriptListener = false;
        PostCommitEventListener listener = listenerList.getSyncPostCommitListeners().get(syncCount);
        isScriptListener = listener instanceof ScriptingPostCommitEventListener;
        assertTrue(isScriptListener);
        desc = serviceImpl.getEventListener("testPostCommit");
        assertEquals(10, desc.getPriority());

        url = EventListenerTest.class.getClassLoader().getResource("test-PostCommitListeners3.xml");
        harness.deployTestContrib("org.nuxeo.ecm.core.event", url);
        assertEquals(asyncCount+1, listenerList.getAsyncPostCommitListeners().size());
        assertEquals(syncCount, listenerList.getSyncPostCommitListeners().size());

        listener = listenerList.getAsyncPostCommitListeners().get(asyncCount);
        isScriptListener = listener instanceof ScriptingPostCommitEventListener;
        assertFalse(isScriptListener);
        desc = serviceImpl.getEventListener("testPostCommit");
        assertEquals(20, desc.getPriority());
    }

}

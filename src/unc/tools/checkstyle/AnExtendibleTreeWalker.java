package unc.tools.checkstyle;


////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2015 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////


import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.core.resources.IWorkspace;


import org.eclipse.core.resources.ResourcesPlugin;

import sun.security.action.GetLongAction;
import antlr.TokenStreamRecognitionException;

import com.puppycrawl.tools.checkstyle.DefaultContext;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.Utils;
import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;


/**
 * Responsible for walking an abstract syntax tree and notifying interested
 * checks at each each node.
 *
 * @author Oliver Burn
 */
public  class AnExtendibleTreeWalker
    extends AbstractFileSetCheck {
	TreeWalker delegate;
	Object delegateCache;
	Method cacheCheckedOk;
//	Method cacheAlreadyChecked;
	Method delegateProcessFiltered;
//	Method cacheDestroy;


//	  PropertyCacheFile cache = new PropertyCacheFile(null, null);
	public AnExtendibleTreeWalker() {
		Field delegateCacheField;
		Field delegateMessageField;
//		Field myMessageField; // same as delegateMessageField
		delegate = new TreeWalker();
		delegate.setSeverity(getSeverity());
		
		try {
			delegateProcessFiltered = delegate.getClass().
					getDeclaredMethod("processFiltered", new Class[] {
							File.class, List.class
					});
			delegateProcessFiltered.setAccessible(true);
//			delegateCacheField = delegate.getClass()
//					.getDeclaredField("cache");
			delegateCacheField = UNCCheckStyleUtil.getDeclaredField (delegate.getClass(),
					"cache");
			delegateCacheField.setAccessible(true);
//			delegateMessageField = AbstractFileSetCheck.class.getDeclaredField("messages");
			delegateMessageField = UNCCheckStyleUtil.getDeclaredField (AbstractFileSetCheck.class,
					"messages");
			delegateMessageField.setAccessible(true);
		
			delegateMessageField.set(delegate, delegateMessageField.get(this));
			
			delegateCache = delegateCacheField.get(delegate);
			Class delegateCacheClass = delegateCache.getClass();
//			cacheAlreadyChecked = delegateCacheClass.getDeclaredMethod
//					("alreadyChecked", new Class[]{String.class, Long.TYPE});
////			cacheDestroy = delegateCacheClass.getDeclaredMethod
////					("destroy");
//			cacheAlreadyChecked.setAccessible(true);
//			System.out.println (" found the methods");
			cacheCheckedOk = delegateCacheClass.getDeclaredMethod
					("checkedOk", new Class[]{String.class, Long.TYPE});
//			cacheDestroy = delegateCacheClass.getDeclaredMethod
//					("destroy");
			cacheCheckedOk.setAccessible(true);
			
			

		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // NoSuchFieldException
		catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
	}
	
	protected boolean ignoreCache(File file) {
		return false;
		// this stuff seems superfluous as teh delegate processFiltered will make the check
//        final long timestamp = file.lastModified();
//        final String fileName = file.getPath();
//      
//        try {
//		return (Boolean) cacheAlreadyChecked.invoke(delegateCache, 
//				new Object[] {fileName, timestamp})	        
////		        if (cache.alreadyChecked(fileName, timestamp)
//					         || !Utils.fileExtensionMatches(file, getFileExtensions());
//        } catch (IllegalAccessException | IllegalArgumentException
//				| InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//        	return false;
//
//		}
	}
	@Override
	protected void processFiltered(File file, List<String> lines) {
		// No need for this as teh delegate does the check itself, we do not need to access it
//		if (ignoreFile(file)) {
//			return;
//
//		}
		
		
//	
//		
//		    // check if already checked and passed the file
//	        final String fileName = file.getPath();
//	        final long timestamp = file.lastModified();
     
	        try {
	        	if (ignoreCache(file)) {
	        		// fool the delegate to think the file is not checked even it if it is
	    	        final long timestamp = file.lastModified() - 1; 
	    	        final String fileName = file.getPath();

	    	        cacheCheckedOk.invoke(delegateCache, 
							new Object[] {fileName, timestamp});

	    			
	    		}
	        	
//	        	// we are not setting the cache, the delegate is, so we do not need this
//				if ((Boolean) cacheAlreadyChecked.invoke(delegateCache, 
//						new Object[] {fileName, timestamp})	        
////	        if (cache.alreadyChecked(fileName, timestamp)
//				         || !Utils.fileExtensionMatches(file, getFileExtensions())) {
//				    return;
//				}
				delegateProcessFiltered.invoke(delegate, new Object[] {file, lines});
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	   
		
	}
//	@Override
//	protected void processFiltered(File file, List<String> lines) {
//		
//		
//	
//		
//		    // check if already checked and passed the file
//	        final String fileName = file.getPath();
//	        final long timestamp = file.lastModified();
//     
//	        try {
//	        	// we are not setting the cache
//				if ((Boolean) cacheAlreadyChecked.invoke(delegateCache, 
//						new Object[] {fileName, timestamp})	        
////	        if (cache.alreadyChecked(fileName, timestamp)
//				         || !Utils.fileExtensionMatches(file, getFileExtensions())) {
//				    return;
//				}
//				delegateProcessFiltered.invoke(delegate, new Object[] {file, lines});
//			} catch (IllegalAccessException | IllegalArgumentException
//					| InvocationTargetException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//	   
//		
//	}
	@Override
    public void finishLocalSetup() {
		delegate.finishLocalSetup();	    
    }

    @Override
    public void setupChild(Configuration childConf)
        throws CheckstyleException {
    
        delegate.setupChild(childConf);
    	
    }
    @Override
    public void destroy() {
      delegate.destroy();
      super.destroy();
    }
    
    public void setModuleFactory(ModuleFactory moduleFactory) {
        delegate.setModuleFactory(moduleFactory);
    }
   
}


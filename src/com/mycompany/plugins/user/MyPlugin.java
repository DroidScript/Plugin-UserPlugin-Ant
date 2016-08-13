
/*
	AndroidScript Plugin class.
	(This is where you put your plugin code)
*/

package com.mycompany.plugins.user;

import android.os.*;
import android.content.*;
import android.util.Log;
import java.util.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.*;
import java.io.*;
import java.lang.reflect.*;

public class MyPlugin
{
	public static String TAG = "MyPlugin";	
	public static float VERSION = 1.0f;	
	private Method m_callscript;
	private Object m_parent;
	private Context m_ctx;

	//Script callbacks.
	private String m_OnMyReply;

	//Contruct plugin.
	public MyPlugin()
	{
		Log.d( TAG, "Creating plugin object");
	}

	//Initialise plugin.
	public void Init( Context ctx, Object parent )
	{
		try {
			Log.d( TAG, "Initialising plugin object");

			//Save context and reference to parent (AndroidScript).
			m_ctx = ctx;
			m_parent = parent;

			//Use reflection to get 'CallScript' method
			Log.d( TAG, "Getting CallScript method");
			m_callscript = parent.getClass().getMethod( "CallScript", Bundle.class );
		 } 
		 catch (Exception e) {
			   Log.e( TAG, "Failed to Initialise plugin!", e );
		 }
	}

	//Release plugin resources.
	public void Release()
	{
	}

	//Call a function in the user's script.
	private void CallScript( Bundle b )
	{
		try {
			m_callscript.invoke( m_parent, b );
		} 
		catch (Exception e) {
			Log.e( TAG, "Failed to call script function!", e );
		}
	}

	//Handle older style commands from DroidScript.
	public String CallPlugin( Bundle b )
	{
		return CallPlugin( b, null );
	}

	//Handle commands from DroidScript.
	public String CallPlugin( Bundle b, Object obj )
	{
		//Extract command.
		String cmd = b.getString("cmd");
	
		//Process commands.
		String ret = null;
		try {
			if( cmd.equals("GetVersion") ) 
				return GetVersion( b );
			else if( cmd.equals("MyFunc") ) 
				MyFunc( b );
			else if( cmd.equals("SetOnMyReply") ) 
				SetOnMyReply( b );
			else if( cmd.equals("SaveMyImage") ) 
				return SaveMyImage( b );
			else if( cmd.equals("ModifyMyImage") ) 
				return ModifyMyImage( b, obj );
		} 
		catch (Exception e) {
		   Log.e( TAG, "Plugin command failed!", e);
		}
		return ret;
	}

	//Handle creating object from DroidScript.
	public Object CreateObject( Bundle b )
	{
		//Extract object type.
		String type = b.getString("type");
	
		//Process commands.
		Object ret = null;
		try {
			if( type.equals("MyButton") ) 
				return CreateMyButton( b );
		} 
		catch (Exception e) {
		   Log.e( TAG, "Plugin command failed!", e);
		}
		return ret;
	}


	//Handle the GetVersion command.
	private String GetVersion( Bundle b )
	{
		Log.d( TAG, "Got GetVersion" );
		return Float.toString( VERSION );
	}
	
	//Handle the 'SetOnMyReply' command.
	private void SetOnMyReply( Bundle b )
	{
		Log.d( TAG, "Got SetOnMyReply" );
		m_OnMyReply = b.getString("p1");
	}

	//Handle the 'MyFunc' command.
	private void MyFunc( Bundle b )
	{
		Log.d( TAG, "Got MyFunc" );

		//Extract params from 'MyFunc' command.
		String txt = b.getString("p1");
		float num = b.getFloat("p2");
		boolean boo = b.getBoolean("p3");
	
		//If 'OnReply' callback is set.
		if( m_OnMyReply!=null )
		{
			//Fire callback in script.
			b = new Bundle();
			b.putString( "cmd", m_OnMyReply );
			b.putString( "p1", txt + " world" );
			b.putFloat( "p2", num+20 );
			b.putBoolean( "p3", !boo );
			CallScript( b );
		}
	}

	//Handle 'SaveImage' command.
	private String SaveMyImage( Bundle b )
	{
		//Get byte array from bundle.
		byte[] byteArray = b.getByteArray("img");

		//Convert image to bitmap.
		Bitmap bmp = BitmapFactory.decodeByteArray( byteArray, 0, byteArray.length );

		//Save image to sdcard.
		String file = "/sdcard/MyPlugin.jpg";
		Log.d( TAG, "Saving jpeg " + file );
		try {
			FileOutputStream outStream = new FileOutputStream( file );	
			bmp.compress( Bitmap.CompressFormat.JPEG, 95, outStream );
			outStream.close();		
		} 
		catch(Exception e) {
			Log.e( TAG, "SaveMyImage failed", e );
			return null;
		} 
		return file;
	}

	//Handle 'ModifyMyImage' command.
	private String ModifyMyImage( Bundle b, Object obj )
	{
		//Extract params.
		String txt = b.getString("p1");
		ImageView v = (ImageView)obj;

		//Create canvas wrapper for image.
		BitmapDrawable drw = (BitmapDrawable)v.getDrawable();
		Bitmap bmp = drw.getBitmap();
		Canvas canv = new Canvas( bmp );

		//Set paint style.
		Paint paint = new Paint();
		paint.setStyle( Paint.Style.FILL );
		paint.setAntiAlias( true );
		paint.setColor( 0xffff00ff );
		paint.setTextSize( 42 );

		//Draw text on image.
        canv.drawText( txt, bmp.getWidth()/3, bmp.getHeight()/3, paint );
		v.invalidate();

		return null;
	}

	//Handle 'CreateMyButton' command.
	private Object CreateMyButton( Bundle b )
	{
		//Extract params.
		String txt = b.getString("p1");
		float width = b.getFloat("p2");
		float height = b.getFloat("p3");

		//Create custom button.
		MyButton btn = new MyButton( m_ctx, txt, width, height );
		return btn;
	}

} 


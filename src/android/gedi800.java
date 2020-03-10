package cordova.plugin.printer.gedi800;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Alignment;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_BarCodeType;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Status;
import br.com.gertec.gedi.interfaces.IGEDI;
import br.com.gertec.gedi.interfaces.IPRNTR;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_BarCodeConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_PictureConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_StringConfig;

/**
 * This class echoes a string called from JavaScript.
 */
public class gedi800 extends CordovaPlugin 
{
	private IPRNTR iPrntr;
    private IGEDI iGEDI;
	private GEDI_PRNTR_e_Status printStatus;
	
	public boolean execute( String action, JSONArray args, CallbackContext callbackContext ) throws JSONException 
	{
        if ( action.equals( "printText" ) ) 
		{            
            this.printText( args.getString( 0 ), callbackContext );
            return true;
        }
		
		if ( action.equals( "printBarcode" ) ) 
		{            
            this.printBarcode( args.getString( 0 ), callbackContext );
            return true;
        }
		
        return false;
    }

    private void printText( String args, CallbackContext callbackContext ) throws JSONException
	{		
		JSONObject json = new JSONObject( args );
		
		String text     = json.getString( "text"     );
		String position = json.getString( "position" );
		String font     = json.getString( "font"     );
		
		int blankLines = Integer.parseInt( json.getString( "blankLines" ) );
		int lineSpace  = Integer.parseInt( json.getString( "lineSpace"  ) );
		int size       = Integer.parseInt( json.getString( "size"       ) );
		
		boolean bold      = Boolean.parseBoolean( json.getString( "bold"      ) ); 
		boolean italic    = Boolean.parseBoolean( json.getString( "italic"    ) );
		boolean underline = Boolean.parseBoolean( json.getString( "underline" ) );
		
		String OK = "ok";
		
        if ( text != null && text.length( ) > 0 ) 
		{
			try 
			{
			    String sMsg = "";
			
				GEDI.init( cordova.getActivity( ) );				   
				
				iGEDI  = GEDI.getInstance( cordova.getActivity( ) );				   
				iPrntr = iGEDI.getPRNTR( );
				
				printStatus = iPrntr.Status( );

				switch ( printStatus ) 
				{
					case OK:
						sMsg = ("STATUS: " + "A impressora está pronta para uso.");
						break;
					case OUT_OF_PAPER:
						sMsg = ("STATUS: " + "A impressora está sem papel ou com tampa aberta.");
						break;
					case OVERHEAT:
						sMsg = ("STATUS: " + "A impressora está superaquecida.");
						break;
					case UNKNOWN_ERROR:
						sMsg = ("STATUS: " + "Valor padrão para erros não mapeados.");
						break;
				}
				
				if ( printStatus == GEDI_PRNTR_e_Status.OK )
				{ 
					iPrntr.Init( );
					
					Paint paint = new Paint( );
					
					paint.setTextSize( size );

					GEDI_PRNTR_st_StringConfig config = new GEDI_PRNTR_st_StringConfig( );
					
					config.lineSpace = lineSpace;
					config.offset    = 10;
					config.paint     = paint;

					iPrntr.DrawStringExt( config, text );
					
					if ( blankLines > 0 )
					{
						iPrntr.DrawBlankLine( blankLines );
					}
					
					iPrntr.Output( );
					
					callbackContext.success( OK );
				} else
				{				
					callbackContext.error( sMsg );
				}
			} catch ( Exception ex ) 
			{
				ex.printStackTrace( );
				callbackContext.error( ex.getMessage( ) );
			}			
        } else 
		{
            callbackContext.error( "Texto invalido para impressao." );
        }
    }
	
	private void printBarcode( String args, CallbackContext callbackContext ) throws JSONException
	{
		JSONObject json = new JSONObject( args );
		
		String text   = json.getString( "text" );
		String type   = json.getString( "type" );
		int    height = Integer.parseInt( json.getString( "height" ) );
		int    width  = Integer.parseInt( json.getString( "width"  ) ); 
		
		String OK = "ok";
		
        if ( text != null && text.length( ) > 0 ) 
		{
			String sMsg = "";
			
			GEDI.init( cordova.getActivity( ) );				   
			
			iGEDI  = GEDI.getInstance( cordova.getActivity( ) );				   
			iPrntr = iGEDI.getPRNTR( );
			
			printStatus = iPrntr.Status( );

			switch ( printStatus ) 
			{
				case OK:
					sMsg = ("STATUS: " + "A impressora está pronta para uso.");
					break;
				case OUT_OF_PAPER:
					sMsg = ("STATUS: " + "A impressora está sem papel ou com tampa aberta.");
					break;
				case OVERHEAT:
					sMsg = ("STATUS: " + "A impressora está superaquecida.");
					break;
				case UNKNOWN_ERROR:
					sMsg = ("STATUS: " + "Valor padrão para erros não mapeados.");
					break;
			}
			
			if ( printStatus == GEDI_PRNTR_e_Status.OK )
			{ 
				iPrntr.Init( );
				
				GEDI_PRNTR_st_BarCodeConfig config = new GEDI_PRNTR_st_BarCodeConfig( );
				
				switch ( type )
			    {
					case "AZTEC":
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.AZTEC;
						break;
					  
					case "CODABAR":
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.CODABAR;
						break;
					  
					case "CODE_128":
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.CODE_128;
						break;
					  
					case "CODE_39":
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.CODE_39;
						break;
					  
					case "CODE_93":
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.CODE_93;
						break;
					  
					case "DATA_MATRIX":
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.DATA_MATRIX;
						break;
					  
					case "EAN_13":
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.EAN_13;
						break;
					  
					case "EAN_8":
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.EAN_8;
						break;
					  
					case "ITF":
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.ITF;
						break;
					  
					case "MAXICODE":
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.MAXICODE;
						break;
					  
					case "PDF_417":						
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.PDF_417;
						break;
					  
					case "RSS_14":
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.RSS_14;
						break;
					  
					case "RSS_EXPANDED":
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.RSS_EXPANDED;
						break;
					  
					case "UPC_A":
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.UPC_A;
						break;
					  
					case "UPC_E":
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.UPC_E;
						break;
					  
					case "UPC_EAN_EXTENSION":
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.UPC_EAN_EXTENSION;
						break;
					  
					default:
						config.barCodeType = GEDI_PRNTR_e_BarCodeType.QR_CODE;
						break;					 
			    }
				
				config.height = height;
				config.width  = width;

				iPrntr.DrawBarCode( config, text );
				
				if ( blankLines > 0 )
				{
					iPrntr.DrawBlankLine( blankLines );
				}
				
				iPrntr.Output( );
				
				callbackContext.success( OK );
			} else
			{				
				callbackContext.error( sMsg );
			}		
        } else 
		{
            callbackContext.error( "Texto invalido para impressao." );
        }
    }
}

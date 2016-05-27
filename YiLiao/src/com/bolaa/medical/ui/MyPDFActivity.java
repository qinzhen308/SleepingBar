package com.bolaa.medical.ui;

import com.bolaa.medical.R;

import android.content.Context;
import android.content.Intent;
import net.sf.andpdf.pdfviewer.PdfViewerActivity;

public class MyPDFActivity extends PdfViewerActivity {

	@Override
	public int getPreviousPageImageResource() {
		return R.drawable.left_arrow;
	}

	@Override
	public int getNextPageImageResource() {
		return R.drawable.right_arrow;
	}

	@Override
	public int getZoomInImageResource() {
		return R.drawable.zoom_in;
	}

	@Override
	public int getZoomOutImageResource() {
		return R.drawable.zoom_out;
	}

	@Override
	public int getPdfPasswordLayoutResource() {
		return R.layout.pdf_file_password;
	}

	@Override
	public int getPdfPageNumberResource() {
		return R.layout.dialog_pagenumber;
	}

	@Override
	public int getPdfPasswordEditField() {
		return R.id.etPassword;
	}

	@Override
	public int getPdfPasswordOkButton() {
		return R.id.btOK;
	}

	@Override
	public int getPdfPasswordExitButton() {
		return R.id.btExit;
	}

	@Override
	public int getPdfPageNumberEditField() {
		return R.id.pagenum_edit;
	}
	
	public static void invoke(Context context,String path){
		Intent intent = new Intent(context, MyPDFActivity.class);
	    intent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, path);
		context.startActivity(intent);
	}

}

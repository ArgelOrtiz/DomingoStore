package com.tec.domingostore.Common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

public class Core {

    public static void crearPDF(Context context,PdfPTable table, String FILE_NAME) {
        Document documento = new Document();

        File file = null;
        try {
            file = new File(context.getFilesDir().getPath(),FILE_NAME);
            file.setExecutable(true);
            file.setReadable(true);
            file.setWritable(true);
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);

            documento.open();

            documento.add(new Paragraph("DomingoStore REPORTES \n\n"));


            documento.add(table);

        }catch(Exception e) {
            Log.e("SellerFragment",e.getMessage());
        } finally {
            documento.close();
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = Uri.parse("content://com.tec.domingostore/files/"+FILE_NAME);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.setType("application/pdf");

        context.startActivity(Intent.createChooser(intent,"Enviar reporte"));
    }
}

package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.entity.Factura;
import com.example.gestion.taller_mecanico.model.entity.MantenimientoProducto;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfGeneratorService {

    public byte[] generateFacturaPdf(Factura factura, List<MantenimientoProducto> productosUsados) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Título
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Font.BOLD);
            Paragraph title = new Paragraph("FACTURA DE MANTENIMIENTO", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Información de la Factura
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);

            document.add(new Paragraph("Factura ID: " + factura.getId(), fontHeader));
            document.add(new Paragraph("Fecha de Emisión: " + factura.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), fontNormal));
            document.add(new Paragraph("\n"));

            // Información del Taller
            document.add(new Paragraph("Información del Taller:", fontHeader));
            document.add(new Paragraph("Nombre: " + factura.getTaller().getNombre(), fontNormal));
            document.add(new Paragraph("Dirección: " + factura.getTaller().getDireccion() + ", " + factura.getTaller().getCiudad(), fontNormal));
            document.add(new Paragraph("\n"));

            // Información del Cliente
            document.add(new Paragraph("Información del Cliente:", fontHeader));
            document.add(new Paragraph("Nombre: " + factura.getCliente().getUsuario().getNombreCompleto(), fontNormal));
            document.add(new Paragraph("Correo: " + factura.getCliente().getUsuario().getCorreo(), fontNormal));
            document.add(new Paragraph("Teléfono: " + factura.getCliente().getTelefono(), fontNormal));
            document.add(new Paragraph("\n"));

            // Información del Vehículo
            document.add(new Paragraph("Información del Vehículo:", fontHeader));
            document.add(new Paragraph("Placa: " + factura.getMantenimiento().getVehiculo().getPlaca(), fontNormal));
            document.add(new Paragraph("Marca: " + factura.getMantenimiento().getVehiculo().getMarca(), fontNormal));
            document.add(new Paragraph("Modelo: " + factura.getMantenimiento().getVehiculo().getModelo(), fontNormal));
            document.add(new Paragraph("\n"));

            // Detalles del Mantenimiento
            document.add(new Paragraph("Detalles del Mantenimiento:", fontHeader));
            document.add(new Paragraph("Servicio: " + factura.getMantenimiento().getServicio().getNombre(), fontNormal));
            document.add(new Paragraph("Precio del Servicio: $" + factura.getMantenimiento().getServicio().getPrecioBase().setScale(2, BigDecimal.ROUND_HALF_UP), fontNormal));
            if (factura.getMantenimiento().getTrabajador() != null) {
                document.add(new Paragraph("Trabajador Asignado: " + factura.getMantenimiento().getTrabajador().getUsuario().getNombreCompleto(), fontNormal));
            }
            document.add(new Paragraph("Estado: " + factura.getMantenimiento().getEstado().name(), fontNormal));
            document.add(new Paragraph("Observaciones: " + factura.getMantenimiento().getObservacionesCliente(), fontNormal));
            document.add(new Paragraph("\n"));

            // Productos Usados
            if (productosUsados != null && !productosUsados.isEmpty()) {
                document.add(new Paragraph("Productos Usados:", fontHeader));
                PdfPTable table = new PdfPTable(4); // Columnas: Producto, Cantidad, Precio Unitario, Subtotal
                table.addCell(new PdfPCell(new Paragraph("Producto", fontNormal)));
                table.addCell(new PdfPCell(new Paragraph("Cantidad", fontNormal)));
                table.addCell(new PdfPCell(new Paragraph("Precio Unitario", fontNormal)));
                table.addCell(new PdfPCell(new Paragraph("Subtotal", fontNormal)));
                for (MantenimientoProducto mp : productosUsados) {
                    table.addCell(new PdfPCell(new Paragraph(mp.getProducto().getNombre(), fontNormal)));
                    table.addCell(new PdfPCell(new Paragraph(String.valueOf(mp.getCantidadUsada()), fontNormal)));
                    table.addCell(new PdfPCell(new Paragraph("$" + mp.getPrecioEnUso().setScale(2, BigDecimal.ROUND_HALF_UP), fontNormal)));
                    table.addCell(new PdfPCell(new Paragraph("$" + mp.getPrecioEnUso().multiply(BigDecimal.valueOf(mp.getCantidadUsada())).setScale(2, BigDecimal.ROUND_HALF_UP), fontNormal)));
                }
                document.add(table);
                document.add(new Paragraph("\n"));
            }

            // Total
            Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.BOLD);
            Paragraph total = new Paragraph("TOTAL: $" + factura.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP), fontTotal);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return baos.toByteArray();
    }
}

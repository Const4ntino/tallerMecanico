package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.entity.Empresa;
import com.example.gestion.taller_mecanico.model.entity.Factura;
import com.example.gestion.taller_mecanico.model.entity.MantenimientoProducto;
import com.example.gestion.taller_mecanico.repository.EmpresaRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BarcodeQRCode;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfGeneratorService {
    
    private final EmpresaRepository empresaRepository;

    public byte[] generateFacturaPdf(Factura factura, List<MantenimientoProducto> productosUsados, boolean conIgv, String ruc) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 54, 36); // Márgenes: izquierda, derecha, superior, inferior

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();
            
            // Obtener la información de la empresa
            Empresa empresa = empresaRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("No se encontró la información de la empresa"));
            
            // Definir fuentes
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
            Font fontSmall = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.BLACK);
            
            // Crear tabla principal con 2 columnas (70% izquierda, 30% derecha)
            PdfPTable mainTable = new PdfPTable(2);
            mainTable.setWidthPercentage(100);
            mainTable.setWidths(new float[]{7, 3});
            
            // CABECERA - LADO IZQUIERDO (INFORMACIÓN DE LA EMPRESA)
            PdfPCell leftHeaderCell = new PdfPCell();
            leftHeaderCell.setBorder(Rectangle.NO_BORDER);
            leftHeaderCell.setPaddingBottom(10);
            
            // Crear una tabla con 2 columnas para el logo y la información de la empresa
            PdfPTable empresaLogoTable = new PdfPTable(2);
            empresaLogoTable.setWidthPercentage(100);
            empresaLogoTable.setWidths(new float[]{2, 8}); // 20% para el logo, 80% para la info
            
            // Celda para el logo
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setPaddingRight(10);
            
            try {
                // Cargar el logo desde la ruta especificada
                String logoPath = empresa.getLogo();
                if (logoPath != null && !logoPath.isEmpty()) {
                    // La ruta del logo es relativa a la raíz del proyecto (donde está el pom.xml)
                    java.io.File file = new java.io.File(logoPath);
                    if (file.exists()) {
                        Image logo = Image.getInstance(file.getAbsolutePath());
                        // Escalar la imagen a un tamaño fijo
                        logo.scaleToFit(80, 80); // Ancho y alto máximos
                        logo.setAlignment(Element.ALIGN_CENTER);
                        
                        logoCell.addElement(logo);
                    }
                }
            } catch (Exception e) {
                // Si hay algún error al cargar la imagen, simplemente continuar sin el logo
                System.err.println("Error al cargar el logo: " + e.getMessage());
            }
            
            empresaLogoTable.addCell(logoCell);
            
            // Celda para la información de la empresa
            PdfPCell infoCell = new PdfPCell();
            infoCell.setBorder(Rectangle.NO_BORDER);
            
            // Tabla para información de la empresa
            PdfPTable empresaTable = new PdfPTable(1);
            empresaTable.setWidthPercentage(100);
            
            // Razón social
            PdfPCell razonCell = new PdfPCell(new Paragraph(empresa.getRazon().toUpperCase(), fontTitle));
            razonCell.setBorder(Rectangle.NO_BORDER);
            razonCell.setPaddingBottom(5);
            empresaTable.addCell(razonCell);
            
            // Dirección
            PdfPCell direccionCell = new PdfPCell(new Paragraph(empresa.getDireccion(), fontNormal));
            direccionCell.setBorder(Rectangle.NO_BORDER);
            direccionCell.setPaddingBottom(3);
            empresaTable.addCell(direccionCell);
            
            // RUC
            PdfPCell rucCell = new PdfPCell(new Paragraph("R.U.C.: " + empresa.getRuc(), fontNormal));
            rucCell.setBorder(Rectangle.NO_BORDER);
            empresaTable.addCell(rucCell);
            
            infoCell.addElement(empresaTable);
            empresaLogoTable.addCell(infoCell);
            
            leftHeaderCell.addElement(empresaLogoTable);
            mainTable.addCell(leftHeaderCell);
            
            // CABECERA - LADO DERECHO (BOLETA ELECTRÓNICA)
            PdfPCell rightHeaderCell = new PdfPCell();
            rightHeaderCell.setBorder(Rectangle.BOX);
            rightHeaderCell.setBorderColor(BaseColor.BLACK);
            rightHeaderCell.setPadding(10);
            rightHeaderCell.setBackgroundColor(new BaseColor(240, 240, 240)); // Gris claro
            
            // Tabla para información de la boleta
            PdfPTable boletaTable = new PdfPTable(1);
            boletaTable.setWidthPercentage(100);
            
            // Título de boleta o factura según si hay RUC
            String titulo = (ruc != null && !ruc.isEmpty()) ? "FACTURA ELECTRÓNICA" : "BOLETA DE VENTA ELECTRÓNICA";
            PdfPCell titleCell = new PdfPCell(new Paragraph(titulo, fontTitle));
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setPaddingBottom(10);
            boletaTable.addCell(titleCell);
            
            // Código de boleta o factura - generar código basado en el ID de la factura
            String prefijo = (ruc != null && !ruc.isEmpty()) ? "FX" : "BX";
            String codigoFactura = prefijo + String.format("%02d", factura.getTaller().getId()) + "-" + 
                                   String.format("%08d", factura.getId());
            System.out.println("codigoFactura generado: " + codigoFactura);
            PdfPCell codigoCell = new PdfPCell(new Paragraph(codigoFactura, fontSubtitle));
            codigoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            codigoCell.setBorder(Rectangle.NO_BORDER);
            boletaTable.addCell(codigoCell);
            
            rightHeaderCell.addElement(boletaTable);
            mainTable.addCell(rightHeaderCell);
            
            // INFORMACIÓN DEL CLIENTE Y DETALLES DE FACTURA
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1, 1});
            infoTable.setSpacingBefore(20);
            
            // Columna izquierda - Información del cliente o RUC según corresponda
            String clienteLabel = (ruc != null && !ruc.isEmpty()) ? "RAZÓN SOCIAL:" : "NOMBRE CLIENTE:";
            PdfPCell clienteHeaderCell = new PdfPCell(new Paragraph(clienteLabel, fontBold));
            clienteHeaderCell.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(clienteHeaderCell);
            
            // Columna derecha - Fecha
            PdfPCell fechaHeaderCell = new PdfPCell(new Paragraph("FECHA:", fontBold));
            fechaHeaderCell.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(fechaHeaderCell);
            
            // Valor nombre cliente o razón social según corresponda
            String clienteValue = (ruc != null && !ruc.isEmpty()) ? 
                                  factura.getCliente().getUsuario().getNombreCompleto().toUpperCase() + " - RUC: " + ruc : 
                                  factura.getCliente().getUsuario().getNombreCompleto().toUpperCase();
            PdfPCell clienteValueCell = new PdfPCell(new Paragraph(clienteValue, fontNormal));
            clienteValueCell.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(clienteValueCell);
            
            // Valor fecha
            PdfPCell fechaValueCell = new PdfPCell(new Paragraph(factura.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fontNormal));
            fechaValueCell.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(fechaValueCell);
            
            // DNI o RUC
            String documentoLabel = (ruc != null && !ruc.isEmpty()) ? "RUC:" : "DNI:";
            PdfPCell dniHeaderCell = new PdfPCell(new Paragraph(documentoLabel, fontBold));
            dniHeaderCell.setBorder(Rectangle.NO_BORDER);
            dniHeaderCell.setPaddingTop(5);
            infoTable.addCell(dniHeaderCell);
            
            // Método de pago
            PdfPCell metodoPagoHeaderCell = new PdfPCell(new Paragraph("MEDIO DE PAGO:", fontBold));
            metodoPagoHeaderCell.setBorder(Rectangle.NO_BORDER);
            metodoPagoHeaderCell.setPaddingTop(5);
            infoTable.addCell(metodoPagoHeaderCell);
            
            // Valor DNI o RUC
            String documentoValue = (ruc != null && !ruc.isEmpty()) ? 
                                  ruc : 
                                  (factura.getCliente().getUsuario().getDni() != null ? factura.getCliente().getUsuario().getDni() : "");
            PdfPCell dniValueCell = new PdfPCell(new Paragraph(documentoValue, fontNormal));
            dniValueCell.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(dniValueCell);
            
            // Valor método de pago
            PdfPCell metodoPagoValueCell = new PdfPCell(new Paragraph(factura.getMetodoPago().name(), fontNormal));
            metodoPagoValueCell.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(metodoPagoValueCell);
            
            // Dirección
            PdfPCell direccionHeaderCell = new PdfPCell(new Paragraph("DIRECCIÓN:", fontBold));
            direccionHeaderCell.setBorder(Rectangle.NO_BORDER);
            direccionHeaderCell.setPaddingTop(5);
            infoTable.addCell(direccionHeaderCell);
            
            // Nro de operación
            PdfPCell nroOperacionHeaderCell = new PdfPCell(new Paragraph("N° OP:", fontBold));
            nroOperacionHeaderCell.setBorder(Rectangle.NO_BORDER);
            nroOperacionHeaderCell.setPaddingTop(5);
            infoTable.addCell(nroOperacionHeaderCell);
            
            // Valor dirección
            String direccion = factura.getCliente().getDireccion() != null ? 
                               factura.getCliente().getDireccion() : "";
            PdfPCell direccionValueCell = new PdfPCell(new Paragraph(direccion, fontNormal));
            direccionValueCell.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(direccionValueCell);
            
            // Valor nro de operación
            String nroOperacion = factura.getNroOperacion() != null ? 
                                 factura.getNroOperacion() : "-";
            PdfPCell nroOperacionValueCell = new PdfPCell(new Paragraph(nroOperacion, fontNormal));
            nroOperacionValueCell.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(nroOperacionValueCell);
            
            // Añadir tabla de información a documento
            document.add(mainTable);
            document.add(infoTable);
            
            // INFORMACIÓN DEL VEHÍCULO
            PdfPTable vehiculoTable = new PdfPTable(2);
            vehiculoTable.setWidthPercentage(100);
            vehiculoTable.setWidths(new float[]{1, 1});
            vehiculoTable.setSpacingBefore(10);
            
            // Placa
            PdfPCell placaHeaderCell = new PdfPCell(new Paragraph("PLACA:", fontBold));
            placaHeaderCell.setBorder(Rectangle.NO_BORDER);
            vehiculoTable.addCell(placaHeaderCell);
            
            // Marca
            PdfPCell marcaHeaderCell = new PdfPCell(new Paragraph("MARCA:", fontBold));
            marcaHeaderCell.setBorder(Rectangle.NO_BORDER);
            vehiculoTable.addCell(marcaHeaderCell);
            
            // Valor placa
            PdfPCell placaValueCell = new PdfPCell(new Paragraph(factura.getMantenimiento().getVehiculo().getPlaca(), fontNormal));
            placaValueCell.setBorder(Rectangle.NO_BORDER);
            vehiculoTable.addCell(placaValueCell);
            
            // Valor marca
            PdfPCell marcaValueCell = new PdfPCell(new Paragraph(factura.getMantenimiento().getVehiculo().getMarca(), fontNormal));
            marcaValueCell.setBorder(Rectangle.NO_BORDER);
            vehiculoTable.addCell(marcaValueCell);
            
            // Modelo
            PdfPCell modeloHeaderCell = new PdfPCell(new Paragraph("MODELO:", fontBold));
            modeloHeaderCell.setBorder(Rectangle.NO_BORDER);
            modeloHeaderCell.setPaddingTop(5);
            vehiculoTable.addCell(modeloHeaderCell);
            
            // Año
            PdfPCell anioHeaderCell = new PdfPCell(new Paragraph("AÑO:", fontBold));
            anioHeaderCell.setBorder(Rectangle.NO_BORDER);
            anioHeaderCell.setPaddingTop(5);
            vehiculoTable.addCell(anioHeaderCell);
            
            // Valor modelo
            PdfPCell modeloValueCell = new PdfPCell(new Paragraph(factura.getMantenimiento().getVehiculo().getModelo(), fontNormal));
            modeloValueCell.setBorder(Rectangle.NO_BORDER);
            vehiculoTable.addCell(modeloValueCell);
            
            // Valor año
            String anio = factura.getMantenimiento().getVehiculo().getAnio() != null ? 
                           factura.getMantenimiento().getVehiculo().getAnio().toString() : "";
            PdfPCell anioValueCell = new PdfPCell(new Paragraph(anio, fontNormal));
            anioValueCell.setBorder(Rectangle.NO_BORDER);
            vehiculoTable.addCell(anioValueCell);
            
            document.add(vehiculoTable);
            
            // TABLA DE PRODUCTOS Y SERVICIOS
            PdfPTable itemsTable = new PdfPTable(7);
            itemsTable.setWidthPercentage(100);
            itemsTable.setWidths(new float[]{0.5f, 1.2f, 3.5f, 0.8f, 0.8f, 0.8f, 1.2f});
            itemsTable.setSpacingBefore(20);
            
            // Encabezados de la tabla
            String[] headers = {"ITEM", "CÓDIGO", "DESCRIPCIÓN", "CANT.", "U.M.", "PRECIO UNIT.", "IMPORTE TOTAL"};
            for (String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Paragraph(header, fontBold));
                headerCell.setBackgroundColor(new BaseColor(220, 220, 220));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headerCell.setPadding(5);
                itemsTable.addCell(headerCell);
            }
            
            // Primero añadimos el servicio
            PdfPCell itemCell = new PdfPCell(new Paragraph("1", fontNormal));
            itemCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemsTable.addCell(itemCell);
            
            PdfPCell codigoServicioCell = new PdfPCell(new Paragraph("SRV" + String.format("%04d", factura.getMantenimiento().getServicio().getId()), fontNormal));
            codigoServicioCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemsTable.addCell(codigoServicioCell);
            
            PdfPCell descripcionServicioCell = new PdfPCell(new Paragraph(factura.getMantenimiento().getServicio().getNombre(), fontNormal));
            itemsTable.addCell(descripcionServicioCell);
            
            PdfPCell cantidadServicioCell = new PdfPCell(new Paragraph("1", fontNormal));
            cantidadServicioCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemsTable.addCell(cantidadServicioCell);
            
            PdfPCell umServicioCell = new PdfPCell(new Paragraph("UND", fontNormal));
            umServicioCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemsTable.addCell(umServicioCell);
            
            BigDecimal precioServicio = factura.getMantenimiento().getServicio().getPrecioBase();
            PdfPCell precioServicioCell = new PdfPCell(new Paragraph(String.format("%.2f", precioServicio), fontNormal));
            precioServicioCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemsTable.addCell(precioServicioCell);
            
            PdfPCell totalServicioCell = new PdfPCell(new Paragraph(String.format("%.2f", precioServicio), fontNormal));
            totalServicioCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemsTable.addCell(totalServicioCell);
            
            // Luego añadimos los productos
            int itemCount = 2;
            BigDecimal totalProductos = BigDecimal.ZERO;
            
            if (productosUsados != null && !productosUsados.isEmpty()) {
                for (MantenimientoProducto mp : productosUsados) {
                    PdfPCell itemProductoCell = new PdfPCell(new Paragraph(String.valueOf(itemCount++), fontNormal));
                    itemProductoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    itemsTable.addCell(itemProductoCell);
                    
                    PdfPCell codigoProductoCell = new PdfPCell(new Paragraph("PRD" + String.format("%04d", mp.getProducto().getId()), fontNormal));
                    codigoProductoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    itemsTable.addCell(codigoProductoCell);
                    
                    PdfPCell descripcionProductoCell = new PdfPCell(new Paragraph(mp.getProducto().getNombre(), fontNormal));
                    itemsTable.addCell(descripcionProductoCell);
                    
                    PdfPCell cantidadProductoCell = new PdfPCell(new Paragraph(String.valueOf(mp.getCantidadUsada()), fontNormal));
                    cantidadProductoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    itemsTable.addCell(cantidadProductoCell);
                    
                    PdfPCell umProductoCell = new PdfPCell(new Paragraph("UND", fontNormal));
                    umProductoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    itemsTable.addCell(umProductoCell);
                    
                    PdfPCell precioProductoCell = new PdfPCell(new Paragraph(String.format("%.2f", mp.getPrecioEnUso()), fontNormal));
                    precioProductoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    itemsTable.addCell(precioProductoCell);
                    
                    BigDecimal subtotal = mp.getPrecioEnUso().multiply(BigDecimal.valueOf(mp.getCantidadUsada()));
                    totalProductos = totalProductos.add(subtotal);
                    
                    PdfPCell totalProductoCell = new PdfPCell(new Paragraph(String.format("%.2f", subtotal), fontNormal));
                    totalProductoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    itemsTable.addCell(totalProductoCell);
                }
            }
            
            document.add(itemsTable);
            
            // Calcular totales con o sin IGV según el parámetro conIgv
            BigDecimal subtotalVenta = precioServicio.add(totalProductos);
            BigDecimal igv = BigDecimal.ZERO;
            BigDecimal totalVenta;
            
            if (conIgv) {
                // Si incluye IGV, calculamos el 18%
                igv = subtotalVenta.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
                totalVenta = subtotalVenta.add(igv);
            } else {
                // Si no incluye IGV, el total es igual al subtotal
                totalVenta = subtotalVenta;
            }
            
            // RESUMEN DE TOTALES (lado derecho)
            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(40);
            totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalsTable.setWidths(new float[]{1.5f, 1});
            totalsTable.setSpacingBefore(15);
            
            // Subtotal
            PdfPCell subtotalLabelCell = new PdfPCell(new Paragraph("SUBTOTAL BOL VENTA", fontNormal));
            subtotalLabelCell.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.BOTTOM);
            subtotalLabelCell.setPadding(5);
            totalsTable.addCell(subtotalLabelCell);
            
            PdfPCell subtotalValueCell = new PdfPCell(new Paragraph(String.format("%.2f", subtotalVenta), fontNormal));
            subtotalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            subtotalValueCell.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM);
            subtotalValueCell.setPadding(5);
            totalsTable.addCell(subtotalValueCell);
            
            // IGV (solo si conIgv es true)
            if (conIgv) {
                PdfPCell igvLabelCell = new PdfPCell(new Paragraph("IGV", fontNormal));
                igvLabelCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
                igvLabelCell.setPadding(5);
                totalsTable.addCell(igvLabelCell);
                
                PdfPCell igvValueCell = new PdfPCell(new Paragraph(String.format("%.2f", igv), fontNormal));
                igvValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                igvValueCell.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
                igvValueCell.setPadding(5);
                totalsTable.addCell(igvValueCell);
            }
            
            // Total
            PdfPCell totalLabelCell = new PdfPCell(new Paragraph("IMPORTE TOTAL S/", fontBold));
            totalLabelCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            totalLabelCell.setPadding(5);
            totalLabelCell.setBackgroundColor(new BaseColor(220, 220, 220));
            totalsTable.addCell(totalLabelCell);
            
            PdfPCell totalValueCell = new PdfPCell(new Paragraph(String.format("%.2f", totalVenta), fontBold));
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValueCell.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
            totalValueCell.setPadding(5);
            totalValueCell.setBackgroundColor(new BaseColor(220, 220, 220));
            totalsTable.addCell(totalValueCell);
            
            document.add(totalsTable);
            
            // Texto de monto en palabras
            String montoEnPalabras = "Son: " + convertirNumeroALetras(totalVenta.doubleValue()) + " SOLES";
            Paragraph montoEnPalabrasParagraph = new Paragraph(montoEnPalabras, fontSmall);
            montoEnPalabrasParagraph.setSpacingBefore(10);
            document.add(montoEnPalabrasParagraph);
            
            // Pie de página con códigos QR y barras
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            footerTable.setWidths(new float[]{1, 1});
            footerTable.setSpacingBefore(20);
            
            // Código QR (incluye RUC del cliente si está disponible)
            String qrContent = codigoFactura + "|" + empresa.getRuc() + "|" + totalVenta.toString();
            if (ruc != null && !ruc.isEmpty()) {
                qrContent += "|" + ruc;
            }
            BarcodeQRCode qrCode = new BarcodeQRCode(qrContent, 100, 100, null);
            Image qrCodeImage = qrCode.getImage();
            qrCodeImage.scalePercent(150);
            
            PdfPCell qrCell = new PdfPCell(qrCodeImage);
            qrCell.setBorder(Rectangle.NO_BORDER);
            qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(qrCell);
            
            // Código de barras
            Barcode128 barcode = new Barcode128();
            barcode.setCode(codigoFactura);
            barcode.setCodeType(Barcode128.CODE128);
            Image barcodeImage = barcode.createImageWithBarcode(writer.getDirectContent(), null, null);
            barcodeImage.scalePercent(100);
            
            PdfPCell barcodeCell = new PdfPCell(barcodeImage);
            barcodeCell.setBorder(Rectangle.NO_BORDER);
            barcodeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(barcodeCell);
            
            document.add(footerTable);
            
            // Texto legal y agradecimiento
            Paragraph legalText = new Paragraph("REPRESENTACIÓN IMPRESA DE LA BOLETA DE VENTA ELECTRÓNICA", fontSmall);
            legalText.setAlignment(Element.ALIGN_CENTER);
            legalText.setSpacingBefore(10);
            document.add(legalText);
            
            Paragraph thankYouText = new Paragraph("Gracias por su compra", fontSmall);
            thankYouText.setAlignment(Element.ALIGN_CENTER);
            thankYouText.setSpacingBefore(5);
            document.add(thankYouText);

        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return baos.toByteArray();
    }
    
    /**
     * Convierte un número a su representación en letras
     * @param numero Número a convertir
     * @return Representación en letras del número
     */
    private String convertirNumeroALetras(double numero) {
        // Separar parte entera y decimal
        long parteEntera = (long) numero;
        int parteDecimal = (int) Math.round((numero - parteEntera) * 100);
        
        return convertirParteEntera(parteEntera) + 
               (parteDecimal > 0 ? " CON " + parteDecimal + "/100" : " CON 00/100");
    }
    
    private String convertirParteEntera(long numero) {
        final String[] UNIDADES = {
            "", "UN", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE", "DIEZ",
            "ONCE", "DOCE", "TRECE", "CATORCE", "QUINCE", "DIECISÉIS", "DIECISIETE", "DIECIOCHO", "DIECINUEVE"
        };
        
        final String[] DECENAS = {
            "", "DIEZ", "VEINTE", "TREINTA", "CUARENTA", "CINCUENTA", "SESENTA", "SETENTA", "OCHENTA", "NOVENTA"
        };
        
        final String[] CENTENAS = {
            "", "CIENTO", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS", "QUINIENTOS", 
            "SEISCIENTOS", "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS"
        };
        
        if (numero == 0) {
            return "CERO";
        }
        
        if (numero < 0) {
            return "MENOS " + convertirParteEntera(Math.abs(numero));
        }
        
        String resultado = "";
        
        // Millones
        if (numero >= 1000000) {
            resultado = convertirParteEntera(numero / 1000000);
            if (numero / 1000000 == 1) {
                resultado += " MILLÓN ";
            } else {
                resultado += " MILLONES ";
            }
            numero %= 1000000;
        }
        
        // Miles
        if (numero >= 1000) {
            if (numero / 1000 == 1) {
                resultado += "MIL ";
            } else {
                resultado += convertirParteEntera(numero / 1000) + " MIL ";
            }
            numero %= 1000;
        }
        
        // Centenas
        if (numero >= 100) {
            if (numero == 100) {
                resultado += "CIEN";
            } else {
                resultado += CENTENAS[(int) (numero / 100)];
            }
            numero %= 100;
            if (numero > 0) {
                resultado += " ";
            }
        }
        
        // Decenas y unidades
        if (numero < 20) {
            resultado += UNIDADES[(int) numero];
        } else {
            resultado += DECENAS[(int) (numero / 10)];
            int unidad = (int) (numero % 10);
            if (unidad > 0) {
                resultado += " Y " + UNIDADES[unidad];
            }
        }
        
        return resultado.trim();
    }
}

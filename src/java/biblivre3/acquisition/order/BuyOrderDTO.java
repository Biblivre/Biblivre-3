/**
 *  Este arquivo é parte do Biblivre3.
 *  
 *  Biblivre3 é um software livre; você pode redistribuí-lo e/ou 
 *  modificá-lo dentro dos termos da Licença Pública Geral GNU como 
 *  publicada pela Fundação do Software Livre (FSF); na versão 3 da 
 *  Licença, ou (caso queira) qualquer versão posterior.
 *  
 *  Este programa é distribuído na esperança de que possa ser  útil, 
 *  mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 *  MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 *  Licença Pública Geral GNU para maiores detalhes.
 *  
 *  Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 *  com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 * 
 *  @author Alberto Wagner <alberto@biblivre.org.br>
 *  @author Danniel Willian <danniel@biblivre.org.br>
 * 
 */

package biblivre3.acquisition.order;

import biblivre3.acquisition.quotation.QuotationDTO;
import java.util.Date;
import mercury.DTO;

public class BuyOrderDTO extends DTO {

    private Integer serial;
    private Integer serialQuotation;
    private Date orderDate;
    private String responsible;
    private String obs;
    private String status;
    private String invoiceNumber;
    private Date receiptDate;
    private Float totalValue;
    private Integer deliveredQuantity;
    private String termsOfPayment;
    private Date deadlineDate;
    private QuotationDTO quotation;

    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(Date deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public Integer getDeliveredQuantity() {
        return deliveredQuantity;
    }

    public void setDeliveredQuantity(Integer deliveredQuantity) {
        this.deliveredQuantity = deliveredQuantity;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public Integer getSerial() {
        return serial;
    }

    public void setSerial(Integer serial) {
        this.serial = serial;
    }

    public Integer getSerialQuotation() {
        return serialQuotation;
    }

    public void setSerialQuotation(Integer serialQuotation) {
        this.serialQuotation = serialQuotation;
    }

    public String getStatus() {
        return (status != null) ? status : "0";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTermsOfPayment() {
        return termsOfPayment;
    }

    public void setTermsOfPayment(String termsOfPayment) {
        this.termsOfPayment = termsOfPayment;
    }

    public Float getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Float totalValue) {
        this.totalValue = totalValue;
    }

    public QuotationDTO getQuotation() {
        return quotation;
    }

    public void setQuotation(QuotationDTO quotation) {
        this.quotation = quotation;
    }

    //Only for search purposes
    transient private Integer serialSupplier;
    public Integer getSerialSupplier() {
        return serialSupplier;
    }
    public void setSerialSupplier(Integer serialSupplier) {
        this.serialSupplier = serialSupplier;
    }

    transient private String supplierName;
    public String getSupplierName() {
        return supplierName;
    }
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

}

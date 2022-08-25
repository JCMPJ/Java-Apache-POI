package org.jcmpj.trt15.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import org.jcmpj.trt15.model.Laudo;
import org.jcmpj.trt15.model.DBManager;
import org.jcmpj.trt15.view.Listagem;

/**
 *
 * @author jcmpj
 */
public class PreencherForm {
    public static Map<String, String> arrRet;
    static Listagem formData;
    static Laudo laudo;

    public PreencherForm(Listagem formData) {
        PreencherForm.formData = formData;
    }
    /**
     * 
     * @return Id e numProcesso
     */
    public static Map<String, String> getLaudoIdNumProcesso() {
        arrRet = new HashMap<>();
        
        arrRet.put("id", Integer.toString(laudo.getId()));
        arrRet.put("numProcesso", laudo.getNumProcesso());
        arrRet.put("nomeReclamante", laudo.getNomeReclamante());
        arrRet.put("nomeReclamada", laudo.getNomeReclamada());
        
        return arrRet;
    }
    /**
     *
     * @description Preenche os campos do formulário dados básicos do laudo.
     *
     */
    public void FillFormData() {
        laudo = new Laudo();
        Pattern p = Pattern.compile("(\\s+[eE]\\s[oO])");
        Matcher m;
        String aux;
        String[] auxRR;
        int row = formData.tblProcessos.getSelectedRow();
        laudo.setId(Integer.parseInt(formData.tblProcessos.getModel().getValueAt(row, 0).toString()));
        laudo.setNumProcesso(formData.tblProcessos.getModel().getValueAt(row, 1).toString());
        laudo.setNomeReclamante(formData.tblProcessos.getModel().getValueAt(row, 2).toString());
        laudo.setNomeReclamada(formData.tblProcessos.getModel().getValueAt(row, 3).toString());

        formData.txtNumProcesso.setText(laudo.getNumProcesso());
        aux = laudo.getNomeReclamante();
        m = p.matcher(aux);
        if (m.find()) {
            formData.txtNomeReclamante.setText(aux.substring(0, m.end() - 3).trim());
        } else {
            formData.txtNomeReclamante.setText(aux.trim());
        }

        aux = laudo.getNomeReclamada();
        m = p.matcher(aux);
        if (m.find()) {
            formData.txtNomeReclamada.setText(aux.substring(0, m.end() - 3).trim());
        } else {
            formData.txtNomeReclamada.setText(aux.trim());
        }

        Map dadosLinha = formData.procs.get(row);
        if (!(dadosLinha.get("dataVistoria") == null)) {
            String dataVistoria = dadosLinha.get("dataVistoria").toString();
            String[] dateaux = dataVistoria.split("-");
            String dia, mes, ano, dma;
            dia = dateaux[2];
            mes = dateaux[1];
            ano = dateaux[0];
            dma = dia + "/" + mes + "/" + ano;
            formData.txtDataVistoria.setText(dma);
        } else {
            formData.txtDataVistoria.setText(null);            
        }
        
        if (!(dadosLinha.get("horaVistoria") == null)) {
            String horaVistoria = dadosLinha.get("horaVistoria").toString();
            formData.txtHoraVistoria.setText(horaVistoria);            
        } else {
            formData.txtHoraVistoria.setText(null);            
        }

        if (!(dadosLinha.get("localVistoria") == null)) {
            String localVistoria = dadosLinha.get("localVistoria").toString();
            formData.txtLocalVistoria.setText(localVistoria);            
        } else {
            formData.txtLocalVistoria.setText(null);            
        }
        // ENDEREÇO
        if (!(dadosLinha.get("enderecoVistoria") == null)) {
            String localVistoria = dadosLinha.get("enderecoVistoria").toString();
            formData.txtEnderecoVistoria.setText(localVistoria);
            
        } else {
            formData.txtEnderecoVistoria.setText(null);
            
        }
        // PERÍODO
        if (!(dadosLinha.get("periodoReclamado") == null)) {
            String localVistoria = dadosLinha.get("periodoReclamado").toString();
            formData.txtPeriodoReclamado.setText(localVistoria);
            
        } else {
            formData.txtPeriodoReclamado.setText(null);
            
        }
        // FUNÇÃO
        if (!(dadosLinha.get("funcaoExercida") == null)) {
            String localVistoria = dadosLinha.get("funcaoExercida").toString();
            formData.txtFuncaoExercida.setText(localVistoria);
            
        } else {
            formData.txtFuncaoExercida.setText(null);
            
        }
    }
    
    public String sqLiteDate(String date) {        
        String[] dma = date.split("/");
        String dia = dma[0];
        String mes = dma[1];
        String ano = dma[2];
        String amd = ano + "-" + mes + "-" + dia;
        
        return amd;
    }
    /**
     * 
     * 000(0) - Sem atualização;
     * 001(1) - Atualiza a data;
     * 010(2) - Atualiza a hora;
     * 100(4) - Atualiza o local
     * 011(3) - Atualiza a data E a hora;
     * 101(5) - Atualiza a data E o local;
     * 110(6) - Atualiza a hora E o local;
     * 111(7) - Atualiza a data, a hora e o local;
     * 
     */
    public void saveFormData() {
        int row = formData.tblProcessos.getSelectedRow();
        Map dadosLinha = formData.procs.get(row);
        
        Map<String, String> dadosUpdate = new HashMap<>();
        int fieldsForUpdate = 0;
        boolean up;
        // DATA
        if (formData.txtDataVistoria.getText() != null
                && !formData.txtDataVistoria.getText().isEmpty()
                && ((dadosLinha.get("dataVistoria") == null)
                || !formData.txtDataVistoria.getText().equals(dadosLinha.get("dataVistoria").toString()))) {
            
            fieldsForUpdate += 1;
            dadosUpdate.put("dataVistoria", sqLiteDate(formData.txtDataVistoria.getText()));
            //up = DBManager.updateByFieldAndValue("dataVistoria", formData.txtDataVistoria.getText(), (int) dadosLinha.get("id"));
        }
        // HORA
        if (formData.txtHoraVistoria.getText() != null
                && !formData.txtHoraVistoria.getText().isEmpty()
                && ((dadosLinha.get("horaVistoria") == null)
                || !formData.txtHoraVistoria.getText().equals(dadosLinha.get("horaVistoria").toString()))) {
            
            fieldsForUpdate += 2;
            dadosUpdate.put("horaVistoria", formData.txtHoraVistoria.getText());
            //up = DBManager.updateByFieldAndValue("horaVistoria", formData.txtHoraVistoria.getText(), (int) dadosLinha.get("id"));
        }
        // LOCAL
        if (formData.txtLocalVistoria.getText() != null
                && !formData.txtLocalVistoria.getText().isEmpty()
                && ((dadosLinha.get("localVistoria") == null)
                || !formData.txtLocalVistoria.getText().equals(dadosLinha.get("localVistoria").toString()))) {
            
            fieldsForUpdate += 4;
            dadosUpdate.put("localVistoria", formData.txtLocalVistoria.getText());
            //up = DBManager.updateByFieldAndValue("localVistoria", formData.txtLocalVistoria.getText(), (int) dadosLinha.get("id"));
        }        
        // ENDEREÇO VISTORIA
        if (formData.txtEnderecoVistoria.getText() != null
                && !formData.txtEnderecoVistoria.getText().isEmpty()
                ) {
            
            DBManager.updateByFieldAndValue("enderecoVistoria", formData.txtEnderecoVistoria.getText(), (int) dadosLinha.get("id"));
        }
        // PERIODO RECLAMADO
        if (formData.txtPeriodoReclamado.getText() != null
                && !formData.txtPeriodoReclamado.getText().isEmpty()
                ) {
            
            DBManager.updateByFieldAndValue("periodoReclamado", formData.txtPeriodoReclamado.getText(), (int) dadosLinha.get("id"));
        }
        // FUNÇÃO EXERCIDA
        if (formData.txtFuncaoExercida.getText() != null
                && !formData.txtFuncaoExercida.getText().isEmpty()
                ) {
            
            DBManager.updateByFieldAndValue("funcaoExercida", formData.txtFuncaoExercida.getText(), (int) dadosLinha.get("id"));
        }
        up = DBManager.updateFields(fieldsForUpdate, dadosUpdate, (int) dadosLinha.get("id"));
        if (up) {
            JOptionPane.showMessageDialog(null, "Dados atualizados.", "Update!", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

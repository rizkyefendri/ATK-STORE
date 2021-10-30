/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
/**
 *
 * @author user
 */
public class FrmAnEMerk extends javax.swing.JFrame {
    
    Connection conn;
    Statement stat;
    public boolean Tambah = true;
    public String Id;
    public FrmAnEMerk() {
        initComponents();
        setLayout();
        Koneksi DB = new Koneksi();
        DB.config();
        conn = DB.conn;
        stat = DB.stat;
        setField(Tambah, Id);
        itemHide();         
    }
    
    private void setLayout(){
        this.setBackground(new Color(0,0,0,0));        
        this.setExtendedState(MAXIMIZED_BOTH);
        fullScreen(bg); 
    }
    
    public void setField(boolean tambah, String id){
        this.Tambah = tambah;
        if(Tambah){
            title.setText("Tambah Merk");
            lb_simpan.setText("Simpan");
            kosong();
            autonumber();
        }else{
            this.Id = id;
            title.setText("Edit Merk");
            lb_simpan.setText("Simpan Perubahan");
            try{
                String sql = "select * from merk where kode_merk= '"+Id+"'";
                Statement stm=conn.createStatement();
                ResultSet res=stm.executeQuery(sql);
                while(res.next()){
                    mrk_id.setText(Id);
                    mrk_nama.setText(res.getString("nama_merk"));
                }
            }catch (SQLException e){
                e.getMessage();
            }
        }
    }
    
    private void fullScreen(JPanel panel){
        Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
        panel.setSize(sz.width, sz.height);
    }

    public void fadeIn(JFrame fm){
        for (double i=0.0; i<=1.0; i=i+0.1){
            String val = i + "";
            float f = Float.valueOf(val);
            fm.setOpacity(f);
            try{
                Thread.sleep(20);
            }catch (InterruptedException e){        
            }
        }        
    }
   
    public void fadeOut(JFrame fm){
        for (double i=1.0; i>=0.0; i=i-0.1){
            String val = i + "";
            float f = Float.valueOf(val);
            this.setOpacity(f);
            try{
                Thread.sleep(20);
            }catch (Exception e){        
            }
        }
    }
    
    private void kosong(){
        mrk_id.setText("");
        mrk_nama.setText("");    
    }
    
    private void itemHide(){
        inama.hide();
    }
    
    private void autonumber(){
        try {
            String sql = "select kode_merk from merk order by kode_merk asc";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            mrk_id.setText("MRK01");
            while (rs.next()) {
                String kode = rs.getString("kode_merk").substring(3);
                int AN = Integer.parseInt(kode) + 1;
                String Nol = "";
                if(AN<10)
                {Nol = "0";}
                else {Nol = "";}
                mrk_id.setText("MRK" + Nol + AN);
            }
        }catch(NumberFormatException | SQLException e){
            JOptionPane.showMessageDialog(null, "Auto Number Gagal" +e);
        }
    }
    
    public void Warn(JPanel pn, Color c, JLabel lb, boolean x){
        pn.setBackground(c);
        lb.setVisible(x);
    }
    
    public void Save(){
        if(valfield()){    
            if("".equals(mrk_nama.getText().trim())){
                Warn(ulnama, new Color(255, 46, 49), inama, true);            
            }
        }else{
            if(Tambah){                
                    try {
                        PreparedStatement pst=conn.prepareStatement("INSERT INTO merk VALUES(?,?)");                   
                        pst.setString(1, mrk_id.getText());
                        pst.setString(2, mrk_nama.getText());                  
                        pst.executeUpdate();                    
                        Alert al = new Alert("Saved Successfully", "/img/ok.png", new Color(33,191,115));
                        al.setVisible(true);
                        al.fadeOut(al);
                        al.dispose();
                        Dashboard.dataMerk();
                        kosong();
                        autonumber();
                    }catch(SQLException e){
                        e.getMessage();
                    }               
            }else{                
                try {
                    PreparedStatement pst=conn.prepareStatement("UPDATE merk SET nama_merk =? WHERE kode_merk = '"+Id+"'");
                    pst.setString(1, mrk_nama.getText());
                    pst.executeUpdate();
                    Alert al = new Alert("Saved Successfully", "/img/ok.png", new Color(33,191,115));
                    al.setVisible(true);
                    al.fadeOut(al);
                    al.dispose();
                    Dashboard.dataMerk();
                    this.dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Perubahan data Gagal!"+ex.getMessage());
                }
            }
        } 
    }
    
    boolean valfield(){
        return "".equals(mrk_nama.getText().trim());
    }
    
    private void setItemColor(JPanel panel, Color c){
        panel.setBackground(c);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        bg = new javax.swing.JPanel();
        bgMain = new javax.swing.JPanel();
        Main = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        ulkode = new javax.swing.JPanel();
        mrk_id = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JPanel();
        lb_simpan = new javax.swing.JLabel();
        btnCancel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        ulnama = new javax.swing.JPanel();
        mrk_nama = new javax.swing.JTextField();
        inama = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        bg.setBackground(new java.awt.Color(0, 0, 0, 100));
        bg.setLayout(new java.awt.GridBagLayout());

        bgMain.setBackground(new java.awt.Color(0, 0, 0, 0));
        bgMain.setLayout(new java.awt.GridBagLayout());

        Main.setBackground(new java.awt.Color(255, 255, 255));
        Main.setPreferredSize(new java.awt.Dimension(400, 240));

        jPanel1.setBackground(new java.awt.Color(248, 248, 248));

        title.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        title.setForeground(new java.awt.Color(188, 188, 188));
        title.setText("Tambah Merk");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/close_g.png"))); // NOI18N
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(title, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(188, 188, 188));
        jLabel2.setText("Kode Merk");
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 300, 20));

        ulkode.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout ulkodeLayout = new javax.swing.GroupLayout(ulkode);
        ulkode.setLayout(ulkodeLayout);
        ulkodeLayout.setHorizontalGroup(
            ulkodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        ulkodeLayout.setVerticalGroup(
            ulkodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel2.add(ulkode, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 45, 358, 1));

        mrk_id.setEditable(false);
        mrk_id.setBackground(new java.awt.Color(255, 255, 255));
        mrk_id.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        mrk_id.setForeground(new java.awt.Color(25, 181, 254));
        mrk_id.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mrk_id.setBorder(null);
        jPanel2.add(mrk_id, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 360, 25));

        btnSimpan.setBackground(new java.awt.Color(25, 181, 254));
        btnSimpan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSimpan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSimpanMouseClicked(evt);
            }
        });

        lb_simpan.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lb_simpan.setForeground(new java.awt.Color(255, 255, 255));
        lb_simpan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_simpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/simpan.png"))); // NOI18N
        lb_simpan.setText("Simpan");
        lb_simpan.setToolTipText("");
        lb_simpan.setIconTextGap(8);
        lb_simpan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lb_simpanMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lb_simpanMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lb_simpanMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lb_simpanMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lb_simpanMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout btnSimpanLayout = new javax.swing.GroupLayout(btnSimpan);
        btnSimpan.setLayout(btnSimpanLayout);
        btnSimpanLayout.setHorizontalGroup(
            btnSimpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lb_simpan, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
        );
        btnSimpanLayout.setVerticalGroup(
            btnSimpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lb_simpan, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
        );

        btnCancel.setBackground(new java.awt.Color(255, 255, 255));
        btnCancel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(188, 188, 188)));
        btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCancelMouseClicked(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(188, 188, 188));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cancel.png"))); // NOI18N
        jLabel9.setText("Cancel");
        jLabel9.setIconTextGap(8);

        javax.swing.GroupLayout btnCancelLayout = new javax.swing.GroupLayout(btnCancel);
        btnCancel.setLayout(btnCancelLayout);
        btnCancelLayout.setHorizontalGroup(
            btnCancelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
        );
        btnCancelLayout.setVerticalGroup(
            btnCancelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(188, 188, 188));
        jLabel3.setText("Nama Merk");
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 300, 20));

        ulnama.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout ulnamaLayout = new javax.swing.GroupLayout(ulnama);
        ulnama.setLayout(ulnamaLayout);
        ulnamaLayout.setHorizontalGroup(
            ulnamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        ulnamaLayout.setVerticalGroup(
            ulnamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel4.add(ulnama, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 45, 358, 1));

        mrk_nama.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        mrk_nama.setBorder(null);
        mrk_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mrk_namaKeyReleased(evt);
            }
        });
        jPanel4.add(mrk_nama, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 300, 25));

        inama.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        inama.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel4.add(inama, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 25, 25));

        javax.swing.GroupLayout MainLayout = new javax.swing.GroupLayout(Main);
        Main.setLayout(MainLayout);
        MainLayout.setHorizontalGroup(
            MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(MainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(MainLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        MainLayout.setVerticalGroup(
            MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        bgMain.add(Main, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 930;
        gridBagConstraints.ipady = 493;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 11, 0);
        bg.add(bgMain, gridBagConstraints);

        getContentPane().add(bg, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelMouseClicked
fadeOut(this);
this.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_btnCancelMouseClicked

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
fadeIn(this);        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowOpened

    private void btnSimpanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpanMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSimpanMouseClicked

    private void lb_simpanMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lb_simpanMouseEntered
setItemColor(btnSimpan, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_lb_simpanMouseEntered

    private void lb_simpanMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lb_simpanMouseExited
setItemColor(btnSimpan, new Color(25, 181, 254));         // TODO add your handling code here:
    }//GEN-LAST:event_lb_simpanMouseExited

    private void lb_simpanMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lb_simpanMousePressed
setItemColor(btnSimpan, new Color(6, 166, 224));        // TODO add your handling code here:
    }//GEN-LAST:event_lb_simpanMousePressed

    private void lb_simpanMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lb_simpanMouseReleased
setItemColor(btnSimpan, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_lb_simpanMouseReleased

    private void lb_simpanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lb_simpanMouseClicked
Save();
FrmAnEBarang.getMerk();// TODO add your handling code here:
    }//GEN-LAST:event_lb_simpanMouseClicked

    private void mrk_namaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mrk_namaKeyReleased
if(mrk_nama.getText().length()!=0){
    Warn(ulnama, new Color(218, 218, 218), inama, false);
}else Warn(ulnama, new Color(255, 46, 49), inama, true);
    // TODO add your handling code here:
    }//GEN-LAST:event_mrk_namaKeyReleased

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
mrk_nama.requestFocusInWindow();        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowGainedFocus

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
fadeOut(this);
this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel1MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmAnEMerk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmAnEMerk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmAnEMerk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmAnEMerk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmAnEMerk().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Main;
    private javax.swing.JPanel bg;
    private javax.swing.JPanel bgMain;
    private javax.swing.JPanel btnCancel;
    private javax.swing.JPanel btnSimpan;
    private javax.swing.JLabel inama;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lb_simpan;
    private javax.swing.JTextField mrk_id;
    private javax.swing.JTextField mrk_nama;
    private javax.swing.JLabel title;
    private javax.swing.JPanel ulkode;
    private javax.swing.JPanel ulnama;
    // End of variables declaration//GEN-END:variables
}

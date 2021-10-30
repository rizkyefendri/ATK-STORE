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
public class FrmAnESupplier extends javax.swing.JFrame {
    
    Connection conn;
    Statement stat;
    public boolean Tambah = true;
    public String Id;
    public FrmAnESupplier() {
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
            title.setText("Tambah Supplier");
            lb_simpan.setText("Simpan");
            kosong();
            autonumber();
        }else{
            this.Id = id;
            title.setText("Edit Supplier");
            lb_simpan.setText("Simpan Perubahan");
            try{
                String sql = "select * from supplier where kode_supplier= '"+Id+"'";
                Statement stm=conn.createStatement();
                ResultSet res=stm.executeQuery(sql);
                while(res.next()){
                    supp_id.setText(Id);
                    supp_nama.setText(res.getString("nama_supplier"));
                    supp_alamat.setText(res.getString("alamat"));
                    supp_notlp.setText(res.getString("notelp"));
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
        supp_id.setText("");
        supp_nama.setText("");
        supp_alamat.setText("");
        supp_notlp.setText("");      
    }
    
    private void itemHide(){
        inama.hide();
        ialamat.hide();
        inotlp.hide();
    }
    
    private void autonumber(){
        try {
            String sql = "select kode_supplier from supplier order by kode_supplier asc";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            supp_id.setText("SUP01");
            while (rs.next()) {
                String kode = rs.getString("kode_supplier").substring(3);
                int AN = Integer.parseInt(kode) + 1;
                String Nol = "";
                if(AN<10)
                {Nol = "0";}
                else {Nol = "";}
                supp_id.setText("SUP" + Nol + AN);
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
            if("".equals(supp_nama.getText().trim())){
                Warn(ulnama, new Color(255, 46, 49), inama, true);            
            }if("".equals(supp_alamat.getText().trim())){
                Warn(ulalamat, new Color(255, 46, 49), ialamat, true);
            }if("".equals(supp_notlp.getText().trim())){
                Warn(ulnotlp, new Color(255, 46, 49), inotlp, true);
            }
        }else{
            if(Tambah){                
                    try {
                        PreparedStatement pst=conn.prepareStatement("INSERT INTO supplier VALUES(?,?,?,?)");                   
                        pst.setString(1, supp_id.getText());
                        pst.setString(2, supp_nama.getText());
                        pst.setString(3, supp_notlp.getText());
                        pst.setString(4, supp_alamat.getText());                    
                        pst.executeUpdate();                    
                        Alert al = new Alert("Saved Successfully", "/img/ok.png", new Color(33,191,115));
                        al.setVisible(true);
                        al.fadeOut(al);
                        al.dispose();
                        Dashboard.dataSupplier();
                        kosong();
                        autonumber();
                    }catch(SQLException e){
                        e.getMessage();
                    }               
            }else{                
                try {
                    PreparedStatement pst=conn.prepareStatement("UPDATE supplier SET nama_supplier =?, notelp =?, alamat =? WHERE kode_supplier = '"+Id+"'");
                    pst.setString(1, supp_nama.getText());
                    pst.setString(2, supp_notlp.getText());
                    pst.setString(3, supp_alamat.getText());
                    pst.executeUpdate();
                    Alert al = new Alert("Saved Successfully", "/img/ok.png", new Color(33,191,115));
                    al.setVisible(true);
                    al.fadeOut(al);
                    al.dispose();
                    Dashboard.dataSupplier();
                    this.dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Perubahan data Gagal!"+ex.getMessage());
                }
            }
        } 
    }
    
    boolean valfield(){
        return "".equals(supp_nama.getText().trim())||"".equals(supp_alamat.getText().trim())||"".equals(supp_notlp.getText().trim());
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
        supp_id = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JPanel();
        lb_simpan = new javax.swing.JLabel();
        btnCancel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        ulnama = new javax.swing.JPanel();
        supp_nama = new javax.swing.JTextField();
        inama = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        ulalamat = new javax.swing.JPanel();
        supp_alamat = new javax.swing.JTextField();
        ialamat = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        ulnotlp = new javax.swing.JPanel();
        supp_notlp = new javax.swing.JTextField();
        inotlp = new javax.swing.JLabel();

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
        getContentPane().setLayout(new java.awt.BorderLayout());

        bg.setBackground(new java.awt.Color(0, 0, 0, 100));
        bg.setLayout(new java.awt.GridBagLayout());

        bgMain.setBackground(new java.awt.Color(0, 0, 0, 0));
        bgMain.setLayout(new java.awt.GridBagLayout());

        Main.setBackground(new java.awt.Color(255, 255, 255));
        Main.setPreferredSize(new java.awt.Dimension(400, 360));

        jPanel1.setBackground(new java.awt.Color(248, 248, 248));

        title.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        title.setForeground(new java.awt.Color(188, 188, 188));
        title.setText("Tambah Supplier");

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
        jLabel2.setText("Kode Supplier");
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

        supp_id.setEditable(false);
        supp_id.setBackground(new java.awt.Color(255, 255, 255));
        supp_id.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        supp_id.setForeground(new java.awt.Color(25, 181, 254));
        supp_id.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        supp_id.setBorder(null);
        jPanel2.add(supp_id, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 360, 25));

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
        jLabel3.setText("Nama Supplier");
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

        supp_nama.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        supp_nama.setBorder(null);
        supp_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                supp_namaKeyReleased(evt);
            }
        });
        jPanel4.add(supp_nama, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 300, 25));

        inama.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        inama.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel4.add(inama, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 25, 25));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(188, 188, 188));
        jLabel6.setText("Alamat");
        jLabel6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel12.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 300, 20));

        ulalamat.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout ulalamatLayout = new javax.swing.GroupLayout(ulalamat);
        ulalamat.setLayout(ulalamatLayout);
        ulalamatLayout.setHorizontalGroup(
            ulalamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        ulalamatLayout.setVerticalGroup(
            ulalamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel12.add(ulalamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 45, 358, 1));

        supp_alamat.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        supp_alamat.setBorder(null);
        supp_alamat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                supp_alamatKeyReleased(evt);
            }
        });
        jPanel12.add(supp_alamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 300, 25));

        ialamat.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ialamat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel12.add(ialamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 25, 25));

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(188, 188, 188));
        jLabel7.setText("No. Tlp");
        jLabel7.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel14.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 300, 20));

        ulnotlp.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout ulnotlpLayout = new javax.swing.GroupLayout(ulnotlp);
        ulnotlp.setLayout(ulnotlpLayout);
        ulnotlpLayout.setHorizontalGroup(
            ulnotlpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        ulnotlpLayout.setVerticalGroup(
            ulnotlpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel14.add(ulnotlp, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 45, 358, 1));

        supp_notlp.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        supp_notlp.setBorder(null);
        supp_notlp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                supp_notlpKeyReleased(evt);
            }
        });
        jPanel14.add(supp_notlp, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 300, 25));

        inotlp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        inotlp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel14.add(inotlp, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 25, 25));

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
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bgMain.add(Main, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 930;
        gridBagConstraints.ipady = 371;
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
Save();        // TODO add your handling code here:
    }//GEN-LAST:event_lb_simpanMouseClicked

    private void supp_namaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_supp_namaKeyReleased
if(supp_nama.getText().length()!=0){
    Warn(ulnama, new Color(218, 218, 218), inama, false);
}else Warn(ulnama, new Color(255, 46, 49), inama, true);
    // TODO add your handling code here:
    }//GEN-LAST:event_supp_namaKeyReleased

    private void supp_alamatKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_supp_alamatKeyReleased
if(supp_alamat.getText().length()!=0){
    Warn(ulalamat, new Color(218, 218, 218), ialamat, false);
}else Warn(ulalamat, new Color(255, 46, 49), ialamat, true);        // TODO add your handling code here:
    }//GEN-LAST:event_supp_alamatKeyReleased

    private void supp_notlpKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_supp_notlpKeyReleased
if(supp_notlp.getText().length()!=0){
    Warn(ulnotlp, new Color(218, 218, 218), inotlp, false);
}else Warn(ulnotlp, new Color(255, 46, 49), inotlp, true);        // TODO add your handling code here:
    }//GEN-LAST:event_supp_notlpKeyReleased

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
supp_nama.requestFocusInWindow();        // TODO add your handling code here:
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
            java.util.logging.Logger.getLogger(FrmAnESupplier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmAnESupplier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmAnESupplier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmAnESupplier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmAnESupplier().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Main;
    private javax.swing.JPanel bg;
    private javax.swing.JPanel bgMain;
    private javax.swing.JPanel btnCancel;
    private javax.swing.JPanel btnSimpan;
    private javax.swing.JLabel ialamat;
    private javax.swing.JLabel inama;
    private javax.swing.JLabel inotlp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lb_simpan;
    private javax.swing.JTextField supp_alamat;
    private javax.swing.JTextField supp_id;
    private javax.swing.JTextField supp_nama;
    private javax.swing.JTextField supp_notlp;
    private javax.swing.JLabel title;
    private javax.swing.JPanel ulalamat;
    private javax.swing.JPanel ulkode;
    private javax.swing.JPanel ulnama;
    private javax.swing.JPanel ulnotlp;
    // End of variables declaration//GEN-END:variables
}

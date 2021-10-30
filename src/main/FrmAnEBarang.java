/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
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
public class FrmAnEBarang extends javax.swing.JFrame {
    
    public static Connection conn;
    public static Statement stat;
    public boolean Tambah = true;
    public String Id;
    public FrmAnEBarang() {
        initComponents();
        setLayout();
        Koneksi DB = new Koneksi();
        DB.config();
        conn = DB.conn;
        stat = DB.stat;        
        getMerk();
        getSatuan();
        setField(Tambah, Id);
        itemHide();        
    }
    
    private void setLayout(){
        this.setBackground(new Color(0,0,0,0));        
        this.setExtendedState(MAXIMIZED_BOTH);
        fullScreen(bg);
    }

    public static void getMerk(){
        try {
            ResultSet rs = conn.createStatement().executeQuery("select * from merk");
            while(rs.next()){
                cbmerk.addItem(rs.getString("nama_merk"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error " + e);
        }
    }
    
    public static void getSatuan(){
        try {
            ResultSet rs = conn.createStatement().executeQuery("select * from satuan");
            while(rs.next()){
                cbstn.addItem(rs.getString("nama_satuan"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error " + e);
        }
    } 
    
    public void setField(boolean tambah, String id){
        this.Tambah = tambah;
        if(Tambah){
            title.setText("Tambah Barang");
            lb_simpan.setText("Simpan");
            kosong();
            //autonumber();
        }else{
            this.Id = id;
            title.setText("Edit Barang");
            lb_simpan.setText("Simpan Perubahan");
            try{
                String sql = "select * from barang JOIN merk ON barang.kode_merk=merk.kode_merk JOIN satuan ON barang.kode_satuan=satuan.kode_satuan where kode_barang= '"+Id+"'";
                Statement stm=conn.createStatement();
                ResultSet res=stm.executeQuery(sql);
                while(res.next()){
                    tfkdbrg.setText(Id);
                    tfnmbrg.setText(res.getString("nama_barang"));
                    cbmerk.setSelectedItem(res.getString("nama_merk"));
                    cbstn.setSelectedItem(res.getString("nama_satuan"));
                    tfstok.setText(res.getString("stok"));
                    tfhbsatuan.setText(res.getString("hbsatuan"));
                    tfhbpcs.setText(res.getString("hbpcs"));
                    tfhjsatuan.setText(res.getString("hjsatuan"));
                    tfhjpcs.setText(res.getString("hjpcs"));
                    int hjualstn = Integer.valueOf(tfhjsatuan.getText());
                    int hbelistn = Integer.valueOf(tfhbsatuan.getText());
                    int hj = Integer.valueOf(tfhjpcs.getText());
                    int hb = Integer.valueOf(tfhbpcs.getText());
                    tfp1.setText(String.valueOf(hjualstn-hbelistn));
                    tfp2.setText(String.valueOf(hj-hb));
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
        tfkdbrg.setText("");
        tfnmbrg.setText("");
        cbmerk.setSelectedItem(null);
        cbstn.setSelectedItem(null);
        cstn.setSelected(false);
        tfstok.setText("");
        tfstoknew.setText("");
        tfhbsatuan.setText("");
        tfhbpcs.setText("");
        tfhjsatuan.setText("");
        tfhjpcs.setText("");
        tfisi.setText("");
        tfmerk.setText("");
        tfstn.setText("");
        tfp1.setText("");
        tfp2.setText("");       
    }
    
    private void itemHide(){
        inama.hide();
        imerk.hide();
        isatuan.hide();
        istok.hide();
        ihbs.hide();
        ihbp.hide();
        itp1.hide();
        itp2.hide();
        ihjs.hide();
        ihjp.hide();
        tfstoknew.hide();
        tfisi.hide();
        tfmerk.hide();
        tfstn.hide();
    }
    
/*
 *
    private void autonumber() {
        try {
            String sql = "select kode_barang from barang order by kode_barang asc";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            tfkdbrg.setText("BRG0001");
            while (rs.next()) {
                String kdbrg = rs.getString("kode_barang").substring(3);
                int AN = Integer.parseInt(kdbrg) + 1;
                String Nol = "";
                if (AN < 10) {
                    Nol = "000";
                } else if (AN < 100) {
                    Nol = "00";
                } else if (AN < 1000) {
                    Nol = "0";
                } else if (AN < 10000) {
                    Nol = "";
                }
                tfkdbrg.setText("BRG" + Nol + AN);
            }
        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Auto Number Gagal" + e);
        }
    }
 */
    
    public void Warn(JPanel pn, Color c, JLabel lb, boolean x){
        pn.setBackground(c);
        lb.setVisible(x);
    }
    
    public void Save(){
        if(valfield()){    
            if("".equals(tfnmbrg.getText().trim())){
                Warn(ulnama, new Color(255, 46, 49), inama, true);
            }if(cbmerk.getSelectedItem()==null){
                imerk.setVisible(true);
            }if(cbstn.getSelectedItem()==null){
                isatuan.setVisible(true);
            }if("".equals(tfstok.getText().trim())){
                Warn(ulstok, new Color(255, 46, 49), istok, true);
            }if("".equals(tfhbsatuan.getText().trim())){
                Warn(ulhbs, new Color(255, 46, 49), ihbs, true);
            }if("".equals(tfhbpcs.getText().trim())){
                Warn(ulhbp, new Color(255, 46, 49), ihbp, true);
            }if("".equals(tfp1.getText().trim())){
                Warn(ultp1, new Color(255, 46, 49), itp1, true);
            }if("".equals(tfp2.getText().trim())){
                Warn(ultp2, new Color(255, 46, 49), itp2, true);
            }if("".equals(tfhjsatuan.getText().trim())){
                Warn(ulhjs, new Color(255, 46, 49), ihjs, true);
            }if("".equals(tfhjpcs.getText().trim())){
                Warn(ulhjp, new Color(255, 46, 49), ihjp, true);
            }
        }else{
            if(Tambah){              
                try {
                    String sql = "INSERT INTO barang VALUES ('"+tfkdbrg.getText()+"','"+tfnmbrg.getText()+"','"+tfmerk.getText()+"','"+tfstn.getText()+"','"+tfstok.getText()+"','"+tfhbsatuan.getText()+"','"+tfhbpcs.getText()+"','"+tfhjsatuan.getText()+"','"+tfhjpcs.getText()+"')";
                    PreparedStatement pst=conn.prepareStatement(sql);
                    pst.execute();                  
                    Alert al = new Alert("Saved Successfully", "/img/ok.png", new Color(33,191,115));
                    al.setVisible(true);
                    al.fadeOut(al);
                    al.dispose();
                    Dashboard.dataBarang();
                    kosong();
                    //autonumber();
                }catch(SQLException e){
                    Alert al = new Alert("Failed to Save", "/img/fail.png", new Color(255, 46, 49));
                    al.setVisible(true);
                    al.fadeOut(al);
                    al.dispose();
                    JOptionPane.showMessageDialog(this, e);
                }              
            }else{                
                try {
                    String sql ="UPDATE barang SET kode_barang = '"+tfkdbrg.getText()+"', nama_barang = '"+tfnmbrg.getText()+"',kode_merk = '"+tfmerk.getText()+"',kode_satuan = '"+tfstn.getText()+"',stok = '"+tfstok.getText()+"',hbsatuan = '"+tfhbsatuan.getText()+"',hbpcs = '"+tfhbpcs.getText()+"',hjsatuan = '"+tfhjsatuan.getText()+"',hjpcs = '"+tfhjpcs.getText()+"' WHERE kode_barang = '"+Id+"'";
                    PreparedStatement pst=conn.prepareStatement(sql);
                    pst.execute();
                    Alert al = new Alert("Saved Successfully", "/img/ok.png", new Color(33,191,115));
                    al.setVisible(true);
                    al.fadeOut(al);
                    al.dispose();
                    Dashboard.dataBarang();
                    this.dispose();
                } catch (HeadlessException | SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Perubahan data Gagal!"+ex.getMessage());
                }            
            }
        } 
    }

    boolean valfield(){
        return "".equals(tfnmbrg.getText().trim())||cbmerk.getSelectedItem()==null||cbstn.getSelectedItem()==null||"".equals(tfstok.getText().trim())||"".equals(tfhbsatuan.getText().trim())||"".equals(tfhbpcs.getText().trim())||"".equals(tfp1.getText().trim())||"".equals(tfp2.getText().trim())||"".equals(tfhjsatuan.getText().trim())||"".equals(tfhjpcs.getText().trim());
    }
    
    private void setItemColor(JPanel panel, Color c){
        panel.setBackground(c);
    }
    
    public int roundUp(int input){
        return ((input+99)/100)*100;
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
        tfisi = new javax.swing.JTextField();
        tfmerk = new javax.swing.JTextField();
        tfstn = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        ulkode = new javax.swing.JPanel();
        tfkdbrg = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JPanel();
        lb_simpan = new javax.swing.JLabel();
        btnCancel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        ulnama = new javax.swing.JPanel();
        tfnmbrg = new javax.swing.JTextField();
        inama = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        imerk = new javax.swing.JLabel();
        cbmerk = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        isatuan = new javax.swing.JLabel();
        cbstn = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        ulstok = new javax.swing.JPanel();
        tfstok = new javax.swing.JTextField();
        istok = new javax.swing.JLabel();
        cstn = new javax.swing.JCheckBox();
        tfstoknew = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        ulhbs = new javax.swing.JPanel();
        tfhbsatuan = new javax.swing.JTextField();
        ihbs = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        ulhbp = new javax.swing.JPanel();
        tfhbpcs = new javax.swing.JTextField();
        ihbp = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        ultp1 = new javax.swing.JPanel();
        tfp1 = new javax.swing.JTextField();
        itp1 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        ultp2 = new javax.swing.JPanel();
        tfp2 = new javax.swing.JTextField();
        itp2 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        ulhjs = new javax.swing.JPanel();
        tfhjsatuan = new javax.swing.JTextField();
        ihjs = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        ulhjp = new javax.swing.JPanel();
        tfhjpcs = new javax.swing.JTextField();
        ihjp = new javax.swing.JLabel();

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
        Main.setPreferredSize(new java.awt.Dimension(400, 600));

        jPanel1.setBackground(new java.awt.Color(248, 248, 248));

        title.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        title.setForeground(new java.awt.Color(188, 188, 188));
        title.setText("Tambah Barang");

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
                .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfisi, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfmerk, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfstn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(title, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                .addComponent(tfisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(tfmerk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(tfstn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(188, 188, 188));
        jLabel2.setText("Kode Barang");
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

        tfkdbrg.setBackground(new java.awt.Color(255, 255, 255));
        tfkdbrg.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tfkdbrg.setForeground(new java.awt.Color(25, 181, 254));
        tfkdbrg.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfkdbrg.setBorder(null);
        jPanel2.add(tfkdbrg, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 360, 25));

        btnSimpan.setBackground(new java.awt.Color(25, 181, 254));
        btnSimpan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

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
        jLabel3.setText("Nama Barang");
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

        tfnmbrg.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tfnmbrg.setBorder(null);
        tfnmbrg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfnmbrgKeyReleased(evt);
            }
        });
        jPanel4.add(tfnmbrg, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 300, 25));

        inama.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        inama.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel4.add(inama, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 25, 25));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(188, 188, 188));
        jLabel4.setText("Merk");
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel6.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 300, 20));

        imerk.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imerk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel6.add(imerk, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 25, 25));

        cbmerk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbmerkActionPerformed(evt);
            }
        });
        jPanel6.add(cbmerk, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, 220, 25));

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/addd.png"))); // NOI18N
        jLabel12.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel12.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(297, 19, 25, 25));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(188, 188, 188));
        jLabel5.setText("Satuan");
        jLabel5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel8.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 300, 20));

        isatuan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        isatuan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel8.add(isatuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 25, 25));

        cbstn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbstnActionPerformed(evt);
            }
        });
        jPanel8.add(cbstn, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, 220, 25));

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/addd.png"))); // NOI18N
        jLabel11.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel11.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
        });
        jPanel8.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(297, 19, 25, 25));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(188, 188, 188));
        jLabel6.setText("Stok");
        jLabel6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel12.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 300, 20));

        ulstok.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout ulstokLayout = new javax.swing.GroupLayout(ulstok);
        ulstok.setLayout(ulstokLayout);
        ulstokLayout.setHorizontalGroup(
            ulstokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        ulstokLayout.setVerticalGroup(
            ulstokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel12.add(ulstok, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 45, 358, 1));

        tfstok.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tfstok.setBorder(null);
        tfstok.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfstokKeyReleased(evt);
            }
        });
        jPanel12.add(tfstok, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 130, 25));

        istok.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        istok.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel12.add(istok, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 25, 25));

        cstn.setBackground(new java.awt.Color(255, 255, 255));
        cstn.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        cstn.setForeground(new java.awt.Color(188, 188, 188));
        cstn.setText("Satuan");
        cstn.setIconTextGap(8);
        cstn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cstnActionPerformed(evt);
            }
        });
        jPanel12.add(cstn, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 70, -1));
        jPanel12.add(tfstoknew, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 20, 30, -1));

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(188, 188, 188));
        jLabel7.setText("Harga Beli Satuan");
        jLabel7.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel14.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 150, 20));

        ulhbs.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout ulhbsLayout = new javax.swing.GroupLayout(ulhbs);
        ulhbs.setLayout(ulhbsLayout);
        ulhbsLayout.setHorizontalGroup(
            ulhbsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        ulhbsLayout.setVerticalGroup(
            ulhbsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel14.add(ulhbs, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 45, 165, 1));

        tfhbsatuan.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tfhbsatuan.setBorder(null);
        tfhbsatuan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfhbsatuanKeyReleased(evt);
            }
        });
        jPanel14.add(tfhbsatuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 120, 25));

        ihbs.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ihbs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel14.add(ihbs, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, 25, 25));

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(188, 188, 188));
        jLabel8.setText("Harga Beli Pcs");
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel16.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 150, 20));

        ulhbp.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout ulhbpLayout = new javax.swing.GroupLayout(ulhbp);
        ulhbp.setLayout(ulhbpLayout);
        ulhbpLayout.setHorizontalGroup(
            ulhbpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        ulhbpLayout.setVerticalGroup(
            ulhbpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel16.add(ulhbp, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 45, 165, 1));

        tfhbpcs.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tfhbpcs.setBorder(null);
        jPanel16.add(tfhbpcs, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 120, 25));

        ihbp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ihbp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel16.add(ihbp, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, 25, 25));

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));
        jPanel18.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(188, 188, 188));
        jLabel10.setText("Profit Satuan");
        jLabel10.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel18.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 150, 20));

        ultp1.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout ultp1Layout = new javax.swing.GroupLayout(ultp1);
        ultp1.setLayout(ultp1Layout);
        ultp1Layout.setHorizontalGroup(
            ultp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        ultp1Layout.setVerticalGroup(
            ultp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel18.add(ultp1, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 45, 165, 1));

        tfp1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tfp1.setBorder(null);
        tfp1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfp1KeyReleased(evt);
            }
        });
        jPanel18.add(tfp1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 120, 25));

        itp1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        itp1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel18.add(itp1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, 25, 25));

        jPanel19.setBackground(new java.awt.Color(255, 255, 255));
        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(188, 188, 188));
        jLabel13.setText("Profit Pcs");
        jLabel13.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel19.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 150, 20));

        ultp2.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout ultp2Layout = new javax.swing.GroupLayout(ultp2);
        ultp2.setLayout(ultp2Layout);
        ultp2Layout.setHorizontalGroup(
            ultp2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        ultp2Layout.setVerticalGroup(
            ultp2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel19.add(ultp2, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 45, 165, 1));

        tfp2.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tfp2.setBorder(null);
        tfp2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfp2KeyReleased(evt);
            }
        });
        jPanel19.add(tfp2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 120, 25));

        itp2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        itp2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel19.add(itp2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, 25, 25));

        jPanel20.setBackground(new java.awt.Color(255, 255, 255));
        jPanel20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(188, 188, 188));
        jLabel14.setText("Harga Jual Satuan");
        jLabel14.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel20.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 150, 20));

        ulhjs.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout ulhjsLayout = new javax.swing.GroupLayout(ulhjs);
        ulhjs.setLayout(ulhjsLayout);
        ulhjsLayout.setHorizontalGroup(
            ulhjsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        ulhjsLayout.setVerticalGroup(
            ulhjsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel20.add(ulhjs, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 45, 165, 1));

        tfhjsatuan.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tfhjsatuan.setBorder(null);
        tfhjsatuan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfhjsatuanKeyReleased(evt);
            }
        });
        jPanel20.add(tfhjsatuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 120, 25));

        ihjs.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ihjs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel20.add(ihjs, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, 25, 25));

        jPanel21.setBackground(new java.awt.Color(255, 255, 255));
        jPanel21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(188, 188, 188));
        jLabel15.setText("Harga Jual Pcs");
        jLabel15.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel21.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 150, 20));

        ulhjp.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout ulhjpLayout = new javax.swing.GroupLayout(ulhjp);
        ulhjp.setLayout(ulhjpLayout);
        ulhjpLayout.setHorizontalGroup(
            ulhjpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        ulhjpLayout.setVerticalGroup(
            ulhjpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel21.add(ulhjp, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 45, 165, 1));

        tfhjpcs.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tfhjpcs.setBorder(null);
        tfhjpcs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfhjpcsKeyReleased(evt);
            }
        });
        jPanel21.add(tfhjpcs, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 120, 25));

        ihjp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ihjp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel21.add(ihjp, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, 25, 25));

        javax.swing.GroupLayout MainLayout = new javax.swing.GroupLayout(Main);
        Main.setLayout(MainLayout);
        MainLayout.setHorizontalGroup(
            MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(MainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MainLayout.createSequentialGroup()
                        .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(202, 202, 202))
                    .addGroup(MainLayout.createSequentialGroup()
                        .addGroup(MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(MainLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(MainLayout.createSequentialGroup()
                                .addGroup(MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jPanel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                                    .addComponent(jPanel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addContainerGap())))
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
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MainLayout.createSequentialGroup()
                        .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(MainLayout.createSequentialGroup()
                        .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        bgMain.add(Main, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 920;
        gridBagConstraints.ipady = 132;
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

    private void tfnmbrgKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfnmbrgKeyReleased
if(tfnmbrg.getText().length()!=0){
    Warn(ulnama, new Color(218, 218, 218), inama, false);
}else Warn(ulnama, new Color(255, 46, 49), inama, true);
    // TODO add your handling code here:
    }//GEN-LAST:event_tfnmbrgKeyReleased

    private void tfstokKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfstokKeyReleased
if(tfstok.getText().length()!=0){
    Warn(ulstok, new Color(218, 218, 218), istok, false);
    tfstoknew.setText(String.valueOf(tfstok.getText()));
}else {
    Warn(ulstok, new Color(255, 46, 49), istok, true);
    tfstoknew.setText(""); 
}        // TODO add your handling code here:
    }//GEN-LAST:event_tfstokKeyReleased

    private void tfhbsatuanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfhbsatuanKeyReleased
if(tfhbsatuan.getText().length()!=0){
    Warn(ulhbs, new Color(218, 218, 218), ihbs, false);
    Warn(ulhbp, new Color(218, 218, 218), ihbp, false);
    try{
        String hbstn = tfhbsatuan.getText();
        if(hbstn.length()!=0){
            int hbsatuan = Integer.valueOf(hbstn);
            int isi = Integer.valueOf(tfisi.getText());
            int hasil = hbsatuan/isi;
            tfhbpcs.setText(String.valueOf(hasil));
        }else tfhbpcs.setText("");
    }catch (NumberFormatException e){
        JOptionPane.showMessageDialog(rootPane, e);
    }
}else {
    Warn(ulhbs, new Color(255, 46, 49), ihbs, true);
    Warn(ulhbp, new Color(255, 46, 49), ihbp, true);
}        // TODO add your handling code here:
    }//GEN-LAST:event_tfhbsatuanKeyReleased

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
tfkdbrg.requestFocusInWindow();        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowGainedFocus

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
fadeOut(this);
this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel1MouseClicked

    private void tfp1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfp1KeyReleased
if(tfp1.getText().length()!=0){
    Warn(ultp1, new Color(218, 218, 218), itp1, false);
    Warn(ulhjs, new Color(218, 218, 218), ihjs, false);
try{
    String p1temp = tfp1.getText();
    if(p1temp.length()!=0){
        int hbs = Integer.valueOf(tfhbsatuan.getText());
        int p1 = Integer.valueOf(p1temp);
        if ("1".equals(tfisi.getText())){
            tfp2.setText(p1temp);
            tfhjsatuan.setText(String.valueOf(roundUp(hbs+p1)));
            tfhjpcs.setText(String.valueOf(roundUp(hbs+p1)));            
        } else {   
            tfhjsatuan.setText(String.valueOf(roundUp(hbs+p1)));
        }
    }else if("1".equals(tfisi.getText())) {
        tfp2.setText("");
        tfhjsatuan.setText("");
        tfhjpcs.setText("");        
    }else tfhjsatuan.setText("");
}catch (NumberFormatException e){
    JOptionPane.showMessageDialog(rootPane, e);
} 
}else {
    Warn(ultp1, new Color(255, 46, 49), itp1, true);
    Warn(ulhjs, new Color(255, 46, 49), ihjs, true);
}       // TODO add your handling code here:
    }//GEN-LAST:event_tfp1KeyReleased

    private void tfp2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfp2KeyReleased
if(tfp2.getText().length()!=0){
    Warn(ultp2, new Color(218, 218, 218), itp2, false);
    Warn(ulhjp, new Color(218, 218, 218), ihjp, false);
    try{
        String p2temp = tfp2.getText();
        if(p2temp.length()!=0){
                int hbp = Integer.valueOf(tfhbpcs.getText());
                int p2 = Integer.valueOf(p2temp);
                tfhjpcs.setText(String.valueOf(roundUp(hbp+p2)));    
        }else tfhjpcs.setText("");
    }catch (NumberFormatException e){
        JOptionPane.showMessageDialog(rootPane, e);
    }
}else {
    Warn(ultp2, new Color(255, 46, 49), itp2, true);
    Warn(ulhjp, new Color(255, 46, 49), ihjp, true);
}        // TODO add your handling code here:
    }//GEN-LAST:event_tfp2KeyReleased

    private void tfhjsatuanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfhjsatuanKeyReleased
if(tfhjsatuan.getText().length()!=0){
    Warn(ulhjs, new Color(218, 218, 218), ihjs, false);
}else Warn(ulhjs, new Color(255, 46, 49), ihjs, true);         // TODO add your handling code here:
    }//GEN-LAST:event_tfhjsatuanKeyReleased

    private void tfhjpcsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfhjpcsKeyReleased
if(tfhjpcs.getText().length()!=0){
    Warn(ulhjp, new Color(218, 218, 218), ihjp, false);
}else Warn(ulhjp, new Color(255, 46, 49), ihjp, true);         // TODO add your handling code here:
    }//GEN-LAST:event_tfhjpcsKeyReleased

    private void cbmerkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbmerkActionPerformed
if(cbmerk.getSelectedItem()!=null)imerk.setVisible(false); 
    try {
        PreparedStatement ps  = conn.prepareStatement("select * from merk where nama_merk=?");
        ps.setString(1, (String)cbmerk.getSelectedItem());
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {
            tfmerk.setText(rs.getString("kode_merk"));
        }
    } catch (SQLException e) {
            e.getMessage();
    }        // TODO add your handling code here:
    }//GEN-LAST:event_cbmerkActionPerformed

    private void cbstnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbstnActionPerformed
if(cbstn.getSelectedItem()!=null)isatuan.setVisible(false); 
    try {
        PreparedStatement ps  = conn.prepareStatement("select * from satuan where nama_satuan=?");
        ps.setString(1, (String)cbstn.getSelectedItem());
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {
            tfstn.setText(rs.getString("kode_satuan"));
            tfisi.setText(rs.getString("isi"));
        }
   } catch (SQLException e) {
       e.getMessage();
   }         // TODO add your handling code here:
    }//GEN-LAST:event_cbstnActionPerformed

    private void cstnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cstnActionPerformed
try{
    int isi = Integer.valueOf(tfisi.getText());
    int stok = Integer.valueOf(tfstoknew.getText());
        if (cstn.isSelected()){
            tfstok.setText(String.valueOf(stok*isi));
        }else if(!cstn.isSelected()){
            tfstok.setText(String.valueOf(stok));
        }
}catch(NumberFormatException e){
    JOptionPane.showMessageDialog(rootPane, e);
}        // TODO add your handling code here:
    }//GEN-LAST:event_cstnActionPerformed

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
FrmAnEMerk fr = new FrmAnEMerk();  
fr.setField(true, "");
fr.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel12MouseClicked

    private void jLabel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseClicked
FrmAnESatuan fr = new FrmAnESatuan();  
fr.setField(true, "");
fr.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel11MouseClicked

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
            java.util.logging.Logger.getLogger(FrmAnEBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmAnEBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmAnEBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmAnEBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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
                new FrmAnEBarang().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Main;
    private javax.swing.JPanel bg;
    private javax.swing.JPanel bgMain;
    private javax.swing.JPanel btnCancel;
    private javax.swing.JPanel btnSimpan;
    private static javax.swing.JComboBox<String> cbmerk;
    private static javax.swing.JComboBox<String> cbstn;
    private javax.swing.JCheckBox cstn;
    private javax.swing.JLabel ihbp;
    private javax.swing.JLabel ihbs;
    private javax.swing.JLabel ihjp;
    private javax.swing.JLabel ihjs;
    private javax.swing.JLabel imerk;
    private javax.swing.JLabel inama;
    private javax.swing.JLabel isatuan;
    private javax.swing.JLabel istok;
    private javax.swing.JLabel itp1;
    private javax.swing.JLabel itp2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel lb_simpan;
    private javax.swing.JTextField tfhbpcs;
    private javax.swing.JTextField tfhbsatuan;
    private javax.swing.JTextField tfhjpcs;
    private javax.swing.JTextField tfhjsatuan;
    private javax.swing.JTextField tfisi;
    private javax.swing.JTextField tfkdbrg;
    private javax.swing.JTextField tfmerk;
    private javax.swing.JTextField tfnmbrg;
    private javax.swing.JTextField tfp1;
    private javax.swing.JTextField tfp2;
    private javax.swing.JTextField tfstn;
    private javax.swing.JTextField tfstok;
    private javax.swing.JTextField tfstoknew;
    private javax.swing.JLabel title;
    private javax.swing.JPanel ulhbp;
    private javax.swing.JPanel ulhbs;
    private javax.swing.JPanel ulhjp;
    private javax.swing.JPanel ulhjs;
    private javax.swing.JPanel ulkode;
    private javax.swing.JPanel ulnama;
    private javax.swing.JPanel ulstok;
    private javax.swing.JPanel ultp1;
    private javax.swing.JPanel ultp2;
    // End of variables declaration//GEN-END:variables
}

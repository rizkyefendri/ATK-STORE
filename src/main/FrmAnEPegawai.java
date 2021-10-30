/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author user
 */
public class FrmAnEPegawai extends javax.swing.JFrame {
    
    private AbstractBorder circleBorder = new CircleBorder();
    Connection conn;
    Statement stat;
    public boolean Tambah = true;
    public String Id;
    public FrmAnEPegawai() {
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
        Border borderz = BorderFactory.createEmptyBorder();
        ((JTextFieldDateEditor)pgw_tgl.getDateEditor()).setBorder(borderz);
        ((JTextFieldDateEditor)pgw_tgl.getDateEditor()).setFont(new Font("SansSerif", Font.PLAIN, 14));
        imgPgw.setBorder(circleBorder); 
        imgPgw.setForeground(new Color (0,0,0,0));
        imgPgw.setBackground(new Color (0,0,0,0));  
    }
    
    public void setField(boolean tambah, String id){
        this.Tambah = tambah;
        if(Tambah){
            title.setText("Tambah Pegawai");
            lb_simpan.setText("Simpan");
            kosong();
            autonumber();
        }else{
            this.Id = id;
            title.setText("Edit Pegawai");
            lb_simpan.setText("Simpan Perubahan");
            try{
                String sql = "select * from pegawai where kode_pegawai= '"+Id+"'";
                Statement stm=conn.createStatement();
                ResultSet res=stm.executeQuery(sql);
                while(res.next()){
                    pgw_id.setText(Id);
                    pgw_nama.setText(res.getString("nama_pegawai"));
                    String tglp = res.getString("tanggal_lahir");
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(tglp);
                    pgw_tgl.setDate(date);
                    String jkel = res.getString("jkel");
                    if (jkel.equals("Laki-Laki")) {
                        jenkel.setSelected(jenkel_lk.getModel(), true);
                    }
                    else jenkel.setSelected(jenkel_pr.getModel(), true);
                    pgw_alamat.setText(res.getString("alamat"));
                    pgw_notlp.setText(res.getString("notlp"));
                    cbJbt.setSelectedItem(res.getString("jabatan"));
                    imgPgw.setIcon(new ImageIcon(new ImageIcon(res.getBytes("foto")).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
                }
            }catch (SQLException | ParseException e){
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
        pgw_id.setText("");
        pgw_nama.setText("");
        pgw_alamat.setText("");
        pgw_notlp.setText("");
        pgw_tgl.setDate(null);
        cbJbt.setSelectedItem(null);
        jenkel.clearSelection();
        imgPgw.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/avatar.png")));        
    }
    
    private void itemHide(){
        inama.hide();
        itgl.hide();
        ijenkel.hide();
        ialamat.hide();
        inotlp.hide();
        ijbt.hide();
        ifoto.hide();
    }
    
    private void autonumber(){
        try {
            String sql = "select kode_pegawai from pegawai order by kode_pegawai asc";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            pgw_id.setText("PGW01");
            while (rs.next()) {
                String kode = rs.getString("kode_pegawai").substring(3);
                int AN = Integer.parseInt(kode) + 1;
                String Nol = "";
                if(AN<10)
                {Nol = "0";}
                else {Nol = "";}
                pgw_id.setText("PGW" + Nol + AN);
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
            if("".equals(pgw_nama.getText().trim())){
                Warn(ulnama, new Color(255, 46, 49), inama, true);
            }if(pgw_tgl.getDate()==null){
                Warn(ultgl, new Color(255, 46, 49), itgl, true);
            }if(jenkel.getSelection()==null){
                ijenkel.setVisible(true);
            }if("".equals(pgw_alamat.getText().trim())){
                Warn(ulalamat, new Color(255, 46, 49), ialamat, true);
            }if("".equals(pgw_notlp.getText().trim())){
                Warn(ulnotlp, new Color(255, 46, 49), inotlp, true);
            }if(cbJbt.getSelectedItem()==null){
                ijbt.setVisible(true);
            }
        }else{
            if(Tambah){
                if(imgPath != null){                
                    try {
                        String jenkel_pgw="";
                        if(jenkel_lk.isSelected()){
                            jenkel_pgw ="Laki-Laki";
                        }else if (jenkel_pr.isSelected()){
                            jenkel_pgw="Perempuan";        }
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String tgl_pgw = sdf.format(pgw_tgl.getDate());
                        InputStream img = new FileInputStream(new File(imgPath));
                        PreparedStatement pst=conn.prepareStatement("INSERT INTO pegawai VALUES(?,?,?,?,?,?,?,?)");                   
                        pst.setString(1, pgw_id.getText());
                        pst.setString(2, pgw_nama.getText());
                        pst.setString(3, tgl_pgw);             
                        pst.setString(4, jenkel_pgw);
                        pst.setString(5, pgw_alamat.getText());
                        pst.setString(6, pgw_notlp.getText());
                        pst.setString(7, cbJbt.getSelectedItem().toString());
                        pst.setBlob(8, img);                    
                        pst.executeUpdate();                    
                        Alert al = new Alert("Saved Successfully", "/img/ok.png", new Color(33,191,115));
                        al.setVisible(true);
                        al.fadeOut(al);
                        al.dispose();
                        Dashboard.dataPegawai();
                        kosong();
                        autonumber();
                    }catch(FileNotFoundException | SQLException e){
                        e.getMessage();
                    }
                }else JOptionPane.showMessageDialog(null, "Silahkan Pilih Foto Dahulu!");                
            }else{
            if(imgPath != null){                
                try {
                    String jenkel_pgw="";
                    if(jenkel_lk.isSelected()){
                        jenkel_pgw ="Laki-Laki";
                    }else if (jenkel_pr.isSelected()){
                        jenkel_pgw="Perempuan";        }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String tgl_pgw = sdf.format(pgw_tgl.getDate());
                    InputStream img = new FileInputStream(new File(imgPath));
                    PreparedStatement pst=conn.prepareStatement("UPDATE pegawai SET nama_pegawai =?,tanggal_lahir =?,jkel =?,alamat =?,notlp =?, jabatan =?,foto =? WHERE kode_pegawai = '"+Id+"'");
                    pst.setString(1, pgw_nama.getText());
                    pst.setString(2, tgl_pgw);             
                    pst.setString(3, jenkel_pgw);
                    pst.setString(4, pgw_alamat.getText());
                    pst.setString(5, pgw_notlp.getText());
                    pst.setString(6, cbJbt.getSelectedItem().toString());
                    pst.setBlob(7, img);
                    pst.executeUpdate();
                    Alert al = new Alert("Saved Successfully", "/img/ok.png", new Color(33,191,115));
                    al.setVisible(true);
                    al.fadeOut(al);
                    al.dispose();
                    Dashboard.dataPegawai();
                    this.dispose();
                } catch (HeadlessException | FileNotFoundException | SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Perubahan data Gagal!"+ex.getMessage());
                }
            }else{
                try {
                    String jenkel_pgw="";
                    if(jenkel_lk.isSelected()){
                        jenkel_pgw ="Laki-Laki";
                    }else if (jenkel_pr.isSelected()){
                        jenkel_pgw="Perempuan";        }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String tgl_pgw = sdf.format(pgw_tgl.getDate());
                    PreparedStatement pst=conn.prepareStatement("UPDATE pegawai SET nama_pegawai =?,tanggal_lahir =?,jkel =?,alamat =?,notlp =?, jabatan =? WHERE kode_pegawai = '"+Id+"'");
                    pst.setString(1, pgw_nama.getText());
                    pst.setString(2, tgl_pgw);             
                    pst.setString(3, jenkel_pgw);
                    pst.setString(4, pgw_alamat.getText());
                    pst.setString(5, pgw_notlp.getText());
                    pst.setString(6, cbJbt.getSelectedItem().toString());
                    pst.executeUpdate();
                    Alert al = new Alert("Saved Successfully", "/img/ok.png", new Color(33,191,115));
                    al.setVisible(true);
                    al.fadeOut(al);
                    al.dispose();
                    Dashboard.dataPegawai();
                    this.dispose();
                }catch (HeadlessException | SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Perubahan data Gagal!"+ex.getMessage());
                }
            }
            }
        } 
    }
    
    String imgPath = null;
    public ImageIcon ResizeImage(String ImagePath, byte[] pic, JLabel label)
    {
        ImageIcon MyImage = null;
        if(ImagePath != null)
        {
           MyImage = new ImageIcon(ImagePath);
        }else
        {
            MyImage = new ImageIcon(pic);
        }
        Image img = MyImage.getImage();
        Image newImg = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImg);
        return image;
    }
    
    boolean valfield(){
        return "".equals(pgw_nama.getText().trim())||pgw_tgl.getDate()==null||jenkel.getSelection()==null||"".equals(pgw_alamat.getText().trim())||"".equals(pgw_notlp.getText().trim())||cbJbt.getSelectedItem()==null;
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

        jenkel = new javax.swing.ButtonGroup();
        bg = new javax.swing.JPanel();
        bgMain = new javax.swing.JPanel();
        Main = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        ulkode = new javax.swing.JPanel();
        pgw_id = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JPanel();
        lb_simpan = new javax.swing.JLabel();
        btnCancel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        ulnama = new javax.swing.JPanel();
        pgw_nama = new javax.swing.JTextField();
        inama = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        ultgl = new javax.swing.JPanel();
        itgl = new javax.swing.JLabel();
        pgw_tgl = new com.toedter.calendar.JDateChooser();
        jPanel8 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jenkel_lk = new javax.swing.JRadioButton();
        jenkel_pr = new javax.swing.JRadioButton();
        ijenkel = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        ulalamat = new javax.swing.JPanel();
        pgw_alamat = new javax.swing.JTextField();
        ialamat = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        ulnotlp = new javax.swing.JPanel();
        pgw_notlp = new javax.swing.JTextField();
        inotlp = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        imgPgw = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        ifoto = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        cbJbt = new javax.swing.JComboBox<>();
        ijbt = new javax.swing.JLabel();

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
        Main.setPreferredSize(new java.awt.Dimension(400, 720));

        jPanel1.setBackground(new java.awt.Color(248, 248, 248));

        title.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        title.setForeground(new java.awt.Color(188, 188, 188));
        title.setText("Tambah Pegawai");

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
        jLabel2.setText("Kode Pegawai");
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

        pgw_id.setEditable(false);
        pgw_id.setBackground(new java.awt.Color(255, 255, 255));
        pgw_id.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        pgw_id.setForeground(new java.awt.Color(25, 181, 254));
        pgw_id.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        pgw_id.setBorder(null);
        jPanel2.add(pgw_id, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 360, 25));

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
        jLabel3.setText("Nama Pegawai");
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

        pgw_nama.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        pgw_nama.setBorder(null);
        pgw_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pgw_namaKeyReleased(evt);
            }
        });
        jPanel4.add(pgw_nama, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 300, 25));

        inama.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        inama.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel4.add(inama, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 25, 25));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(188, 188, 188));
        jLabel4.setText("Tanggal Lahir");
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel6.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 300, 20));

        ultgl.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout ultglLayout = new javax.swing.GroupLayout(ultgl);
        ultgl.setLayout(ultglLayout);
        ultglLayout.setHorizontalGroup(
            ultglLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        ultglLayout.setVerticalGroup(
            ultglLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel6.add(ultgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 45, 358, 1));

        itgl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        itgl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel6.add(itgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 25, 25));
        jPanel6.add(pgw_tgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 320, -1));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(188, 188, 188));
        jLabel5.setText("Jenis Kelamin");
        jLabel5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel8.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 300, 20));

        jenkel_lk.setBackground(new java.awt.Color(255, 255, 255));
        jenkel.add(jenkel_lk);
        jenkel_lk.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jenkel_lk.setForeground(new java.awt.Color(188, 188, 188));
        jenkel_lk.setText("Laki - Laki");
        jenkel_lk.setIconTextGap(8);
        jenkel_lk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jenkel_lkActionPerformed(evt);
            }
        });
        jPanel8.add(jenkel_lk, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, -1, 25));

        jenkel_pr.setBackground(new java.awt.Color(255, 255, 255));
        jenkel.add(jenkel_pr);
        jenkel_pr.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jenkel_pr.setForeground(new java.awt.Color(188, 188, 188));
        jenkel_pr.setText("Perempuan");
        jenkel_pr.setIconTextGap(8);
        jenkel_pr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jenkel_prActionPerformed(evt);
            }
        });
        jPanel8.add(jenkel_pr, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 130, 25));

        ijenkel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ijenkel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel8.add(ijenkel, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 25, 25));

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

        pgw_alamat.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        pgw_alamat.setBorder(null);
        pgw_alamat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pgw_alamatKeyReleased(evt);
            }
        });
        jPanel12.add(pgw_alamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 300, 25));

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

        pgw_notlp.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        pgw_notlp.setBorder(null);
        pgw_notlp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pgw_notlpKeyReleased(evt);
            }
        });
        jPanel14.add(pgw_notlp, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 300, 25));

        inotlp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        inotlp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel14.add(inotlp, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 25, 25));

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));
        jPanel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(188, 188, 188));
        jLabel8.setText("Foto");
        jPanel16.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 210, 20));

        imgPgw.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        imgPgw.setForeground(new java.awt.Color(188, 188, 188));
        imgPgw.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imgPgw.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));
        imgPgw.setIconTextGap(0);
        imgPgw.setPreferredSize(new java.awt.Dimension(113, 151));
        jPanel16.add(imgPgw, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 27, 120, 120));

        jButton1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jButton1.setText("Pilih Foto");
        jButton1.setBorder(null);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel16.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 70, 70, 25));

        ifoto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ifoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel16.add(ifoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 70, 25, 25));

        jPanel19.setBackground(new java.awt.Color(255, 255, 255));
        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(188, 188, 188));
        jLabel10.setText("Jabatan");
        jLabel10.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel19.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 1, 300, 20));

        cbJbt.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        cbJbt.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "Staf Gudang", "Staf Kasir" }));
        cbJbt.setBorder(null);
        cbJbt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbJbtActionPerformed(evt);
            }
        });
        jPanel19.add(cbJbt, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 230, 25));

        ijbt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ijbt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/warn.png"))); // NOI18N
        jPanel19.add(ijbt, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 25, 25));

        javax.swing.GroupLayout MainLayout = new javax.swing.GroupLayout(Main);
        Main.setLayout(MainLayout);
        MainLayout.setHorizontalGroup(
            MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(MainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(MainLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(MainLayout.createSequentialGroup()
                                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
        gridBagConstraints.ipady = 9;
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser file = new JFileChooser();
        file.setCurrentDirectory(new File(System.getProperty("user.home")));
        //filter the files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg","gif","png");
        file.addChoosableFileFilter(filter);
        int result = file.showSaveDialog(null);
        //if the user click on save in Jfilechooser
        if(result == JFileChooser.APPROVE_OPTION){           
            File selectedFile = file.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            imgPgw.setIcon(ResizeImage(path,null,imgPgw));            
            imgPath = path;
        }
        //if the user click on save in Jfilechooser
        else if(result == JFileChooser.CANCEL_OPTION){
            JOptionPane.showMessageDialog(null, "No File Select!");
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

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

    private void pgw_namaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pgw_namaKeyReleased
if(pgw_nama.getText().length()!=0){
    Warn(ulnama, new Color(218, 218, 218), inama, false);
}else Warn(ulnama, new Color(255, 46, 49), inama, true);
    // TODO add your handling code here:
    }//GEN-LAST:event_pgw_namaKeyReleased

    private void pgw_alamatKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pgw_alamatKeyReleased
if(pgw_alamat.getText().length()!=0){
    Warn(ulalamat, new Color(218, 218, 218), ialamat, false);
}else Warn(ulalamat, new Color(255, 46, 49), ialamat, true);        // TODO add your handling code here:
    }//GEN-LAST:event_pgw_alamatKeyReleased

    private void pgw_notlpKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pgw_notlpKeyReleased
if(pgw_notlp.getText().length()!=0){
    Warn(ulnotlp, new Color(218, 218, 218), inotlp, false);
}else Warn(ulnotlp, new Color(255, 46, 49), inotlp, true);        // TODO add your handling code here:
    }//GEN-LAST:event_pgw_notlpKeyReleased

    private void jenkel_lkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jenkel_lkActionPerformed
if(jenkel.getSelection()!=null)ijenkel.setVisible(false);
    }//GEN-LAST:event_jenkel_lkActionPerformed

    private void pgw_tglPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_pgw_tglPropertyChange
if(pgw_tgl.getDate()!=null)Warn(ultgl, new Color(218, 218, 218), itgl, false);        // TODO add your handling code here:
    }//GEN-LAST:event_pgw_tglPropertyChange

    private void jenkel_prActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jenkel_prActionPerformed
if(jenkel.getSelection()!=null)ijenkel.setVisible(false);        // TODO add your handling code here:
    }//GEN-LAST:event_jenkel_prActionPerformed

    private void cbJbtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbJbtActionPerformed
if(cbJbt.getSelectedItem()!=null)ijbt.setVisible(false);        // TODO add your handling code here:
    }//GEN-LAST:event_cbJbtActionPerformed

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
pgw_nama.requestFocusInWindow();        // TODO add your handling code here:
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
            java.util.logging.Logger.getLogger(FrmAnEPegawai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmAnEPegawai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmAnEPegawai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmAnEPegawai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmAnEPegawai().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Main;
    private javax.swing.JPanel bg;
    private javax.swing.JPanel bgMain;
    private javax.swing.JPanel btnCancel;
    private javax.swing.JPanel btnSimpan;
    private javax.swing.JComboBox<String> cbJbt;
    private javax.swing.JLabel ialamat;
    private javax.swing.JLabel ifoto;
    private javax.swing.JLabel ijbt;
    private javax.swing.JLabel ijenkel;
    private javax.swing.JLabel imgPgw;
    private javax.swing.JLabel inama;
    private javax.swing.JLabel inotlp;
    private javax.swing.JLabel itgl;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.ButtonGroup jenkel;
    private javax.swing.JRadioButton jenkel_lk;
    private javax.swing.JRadioButton jenkel_pr;
    private javax.swing.JLabel lb_simpan;
    private javax.swing.JTextField pgw_alamat;
    private javax.swing.JTextField pgw_id;
    private javax.swing.JTextField pgw_nama;
    private javax.swing.JTextField pgw_notlp;
    private com.toedter.calendar.JDateChooser pgw_tgl;
    private javax.swing.JLabel title;
    private javax.swing.JPanel ulalamat;
    private javax.swing.JPanel ulkode;
    private javax.swing.JPanel ulnama;
    private javax.swing.JPanel ulnotlp;
    private javax.swing.JPanel ultgl;
    // End of variables declaration//GEN-END:variables
}

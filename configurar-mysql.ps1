Add-Type -AssemblyName System.Windows.Forms
Add-Type -AssemblyName System.Drawing

function Escape-Property([string]$value) {
    $escaped = New-Object System.Text.StringBuilder
    foreach ($character in $value.ToCharArray()) {
        if ([int]$character -gt 126 -or [int]$character -lt 32) {
            [void]$escaped.Append(('\u{0:x4}' -f [int]$character))
        } elseif ('\:=#! '.Contains([string]$character)) {
            [void]$escaped.Append('\').Append($character)
        } else {
            [void]$escaped.Append($character)
        }
    }
    return $escaped.ToString()
}

$form = New-Object System.Windows.Forms.Form
$form.Text = 'API - Biblioteca - Configurar MySQL'
$form.ClientSize = New-Object System.Drawing.Size(470, 255)
$form.StartPosition = 'CenterScreen'
$form.FormBorderStyle = 'FixedDialog'
$form.MaximizeBox = $false
$form.Font = New-Object System.Drawing.Font('Segoe UI', 10)

$intro = New-Object System.Windows.Forms.Label
$intro.Text = 'Escribe los datos de tu MySQL local. La contraseña se guarda solo en tu computadora.'
$intro.Location = New-Object System.Drawing.Point(20, 15)
$intro.Size = New-Object System.Drawing.Size(430, 50)
$form.Controls.Add($intro)

$userLabel = New-Object System.Windows.Forms.Label
$userLabel.Text = 'Usuario de MySQL'
$userLabel.Location = New-Object System.Drawing.Point(20, 80)
$userLabel.Size = New-Object System.Drawing.Size(160, 25)
$form.Controls.Add($userLabel)
$user = New-Object System.Windows.Forms.TextBox
$user.Text = 'root'
$user.Location = New-Object System.Drawing.Point(190, 78)
$user.Size = New-Object System.Drawing.Size(250, 25)
$form.Controls.Add($user)

$passwordLabel = New-Object System.Windows.Forms.Label
$passwordLabel.Text = 'Contraseña de MySQL'
$passwordLabel.Location = New-Object System.Drawing.Point(20, 125)
$passwordLabel.Size = New-Object System.Drawing.Size(170, 25)
$form.Controls.Add($passwordLabel)
$password = New-Object System.Windows.Forms.TextBox
$password.UseSystemPasswordChar = $true
$password.Location = New-Object System.Drawing.Point(190, 122)
$password.Size = New-Object System.Drawing.Size(250, 25)
$form.Controls.Add($password)

$save = New-Object System.Windows.Forms.Button
$save.Text = 'Guardar configuración'
$save.Location = New-Object System.Drawing.Point(190, 175)
$save.Size = New-Object System.Drawing.Size(250, 40)
$save.Add_Click({
    if ([string]::IsNullOrWhiteSpace($user.Text)) {
        [System.Windows.Forms.MessageBox]::Show('Escribe el usuario de MySQL.')
        return
    }
    $localFile = Join-Path (Get-Location) 'application-local.properties'
    $content = 'spring.datasource.username=' + (Escape-Property $user.Text) + "`r`n" + 'spring.datasource.password=' + (Escape-Property $password.Text) + "`r`n"
    [IO.File]::WriteAllText($localFile, $content, (New-Object System.Text.UTF8Encoding($false)))
    [System.Windows.Forms.MessageBox]::Show('Configuración guardada. Ahora abre EJECUTAR.cmd. MySQL debe estar iniciado.', 'API - Biblioteca')
    $form.Close()
})
$form.Controls.Add($save)
$form.AcceptButton = $save
[void]$form.ShowDialog()


{
  inputs.nixpkgs.url = "github:nixos/nixpkgs/nixpkgs-unstable";
  inputs.flake-utils.url = "github:numtide/flake-utils";

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
      in
      {
        packages = {
          default = pkgs.mkShell {
            name = "Java Environment";
            buildInputs = [
              (pkgs.sbt.override {
                jre = pkgs.jdk8;
              })
            ];
            JAVA_8_HOME = "${pkgs.jdk8}/lib/openjdk";
            shellHook = ''
              export PATH="$JAVA_8_HOME/bin:$PATH"
            '';
          };
        };
      }
    );
}

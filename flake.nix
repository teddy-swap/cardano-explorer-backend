{
  inputs.nixpkgs.url = "github:nixos/nixpkgs/nixpkgs-unstable";
  outputs = { self, nixpkgs }:
    let pkgs = nixpkgs.legacyPackages.x86_64-darwin;
  in {
    # Development Environment Shell
    # nix develop --command zsh | bash
    packages.x86_64-darwin.default = pkgs.mkShell {
        name = "Java Environment";
        buildInputs = [
          (pkgs.sbt.override {
            jre = pkgs.jdk8;
          })
        ];

        JAVA_8_HOME = "${pkgs.jdk8}/lib/openjdk";
      };
  };
}
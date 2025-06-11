#Szyfrowanie LSB 
Branch structure 
master  - the main branch
staging - the branch where final verifications are made before merging to master 
develop - the branch which consists the backbone of the development work. 
          Still developers should refrain from committing directly to this branch. 
          Instead all the work should be performed on “feature” branches which should be subbranches of the develop branch.

Feature branches (create and delete them when needed)
naming convention  <branch-type>/<branch-name>
examples:
feature/LSB_alg
feature/Text_encryption
test/UI
fix/file_handeling
